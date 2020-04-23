package Entities;

import Enumeration.JobReturnValue;
import Enumeration.JobStatus;
import Enumeration.LoggerPriority;
import Messages.ProposeJobMessage;
import Messages.UpdateTableMessage;
import Network.Broadcaster;
import Main.ExecutorMain;
import Network.SocketSenderUnicast;
import utils.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Executor {
    // keeps all executed job. It's persistent and it's lazily load at startup time
    private Map<String, Job> idToJob;
    // keeps jobs that are being handled
    private Map<String, Job> idToActiveJobs;
    private Map<InetAddress, Pair<Integer, Integer>> executorToInfos;
    private Map<String, InetAddress> foreignCompletedJobs;
    private java.util.concurrent.Executor executorService;
    private CompletionService<Pair<String, Object>> executorCompletionService;
    private CallbackThread ct;
    private ArrayList<InetAddress> knownExecutors;                          // is persistent

    public static Executor instance = null;

    public static Executor getIstance() {
        if(instance==null)
            synchronized(Executor.class) {
                if( instance == null )
                    instance = new Executor();
            }
        return instance;
    }

    private Executor() {
        this.executorToInfos = new ConcurrentHashMap<InetAddress, Pair<Integer, Integer>>();
        this.executorToInfos.put(ExecutorMain.localIP, new Pair<>(0, ExecutorMain.nThreads));
        this.foreignCompletedJobs = new ConcurrentHashMap<String, InetAddress>();
        this.executorService = Executors.newFixedThreadPool(ExecutorMain.nThreads);
        this.executorCompletionService = new ExecutorCompletionService<>(executorService);
        this.idToJob = new LazyHashMap<String, Job>(ExecutorMain.relativePathToArchiveDir);
        this.idToActiveJobs = new ConcurrentHashMap<>();
        this.runUncompletedJobs();
        this.ct = new CallbackThread();
        this.ct.start();
        this.knownExecutors = new ArrayList<>();
    }

    public synchronized void addExecutor(InetAddress address, Integer jobs, Integer nThreads){
        if (!this.executorToInfos.containsKey(address)) {
            this.executorToInfos.put(address, new Pair<>(jobs, nThreads));
        }
        if (!this.knownExecutors.contains(address)){                    // if a new executor connects, add it to the list of know host
            if (address.equals(ExecutorMain.localIP)){
                return;
            }
            this.knownExecutors.add(address);
        }
        Logger.log(LoggerPriority.NOTIFICATION, "Connected executor @"  + address);
        printState();
    }

    public synchronized void removeExecutor(InetAddress address){
        executorToInfos.remove(address);
        Logger.log(LoggerPriority.NOTIFICATION, "Executor @"  + address + " leaved");
        printState();
    }

    private InetAddress getMinKey(Map<InetAddress, Pair<Integer, Integer>> map) {
        InetAddress minKey = null;
        int minValue = Integer.MAX_VALUE;
        for(InetAddress key : map.keySet()) {
            int value = map.get(key).first;
            if(value < minValue) {
                minValue = value;
                minKey = key;
            }
        }
        return minKey;
    }

    public InetAddress proposeJob(){
        return getMinKey(this.executorToInfos);
    }

    public void acceptJobs(ArrayList<Job> jobs) {
        ArrayList<String> acceptedJobsIds = new ArrayList<>();
        for (Job job : jobs) {
            Logger.log(LoggerPriority.NOTIFICATION, "EXECUTOR: Adding job of type " + job.getType() + " added to the job queue (id: " + job.getID() + ")");

            job.setStatus(JobStatus.PENDING);
            this.idToJob.put(job.getID(), job);
            this.idToActiveJobs.put(job.getID(), job);
            executorCompletionService.submit(job);
            incrementJobs(false);

            acceptedJobsIds.add(job.getID());
        }
        printState();
        UpdateTableMessage msg = new UpdateTableMessage(getNumberOfJobs(), acceptedJobsIds);
        Broadcaster.getInstance().send(msg);
    }

    private int numberOfAccebtableJobs(InetAddress idleExecutor){
        Integer availableSlots = executorToInfos.get(idleExecutor).second;
        Long nOfNeededReassignament = executorToInfos.values().stream()
                                                                .filter(value -> value.first > value.second)
                                                                .count();
        Integer toReturn = 0;
        if(nOfNeededReassignament != 0){
            if(availableSlots >= nOfNeededReassignament){
                // We have more slots available than the number of needed reassignaments --> Each executor will send more than one job
                toReturn = (int) (availableSlots/nOfNeededReassignament);
            } else {
                // We have less slots available than the number of needed reassignaments --> Only #availableSlots executors will balance the load
                ArrayList<InetAddress> orderedList = (ArrayList<InetAddress>) executorToInfos.keySet().stream()
                        .sorted()
                        .limit(availableSlots)
                        .collect(Collectors.toList());
                if(orderedList.contains(ExecutorMain.localIP)) {
                    toReturn = 1;
                }
            }
        }
        return toReturn;
    }

    public void reassignJobs(InetAddress idleExecutor){
        ArrayList<Job> jobToReassign = new ArrayList<>();
        Integer maxN = numberOfAccebtableJobs(idleExecutor);
        if(maxN == 0){
            return;
        }
        if (this.getNumberOfJobs() > ExecutorMain.nThreads ){
            for (Job j : this.idToActiveJobs.values()){
                if(j.getStatus() == JobStatus.PENDING){
                    jobToReassign.add(j);
                    if (jobToReassign.size() == maxN)
                        break;
                }
            }
            try {
                Logger.log(LoggerPriority.NOTIFICATION, "Found " + jobToReassign.size() + " jobs to reassign");
                ProposeJobMessage pjb = new ProposeJobMessage(jobToReassign);
                SocketSenderUnicast.send(pjb, idleExecutor, ExecutorMain.executorsPort);
                for (Job job : jobToReassign) {
                    job.setStatus(JobStatus.ABORTED);
                    decrementJobs(false);
                    Logger.log(LoggerPriority.NOTIFICATION, "Correctly reassigned job with id: " + job.getID() + "to " + idleExecutor);
                }
                //printState();
            } catch (IOException | ClassNotFoundException e) {
                Logger.log(LoggerPriority.ERROR, "(not fatal) Impossible to handle job reassignment. Continuing");
            } catch (NullPointerException e){
                Logger.log(LoggerPriority.DEBUG, "no available jobs for reassignment");
                return;
            }

            UpdateTableMessage utm = new UpdateTableMessage(this.getNumberOfJobs());
            Broadcaster.getInstance().send(utm);
        }
    }

    public Map<InetAddress, Pair<Integer,Integer>> getExecutorToInfos() { return executorToInfos; }

    public Integer getNumberOfJobs() {
        return this.executorToInfos.get(ExecutorMain.localIP).first;
    }

    public Map<String, InetAddress> getForeignCompletedJobs() { return foreignCompletedJobs; }

    private void incrementJobs(Boolean verbose){
        this.executorToInfos.get(ExecutorMain.localIP).first = this.getNumberOfJobs() + 1;
        if (verbose) printState();
    }

    private void decrementJobs(Boolean verbose){
        this.executorToInfos.get(ExecutorMain.localIP).first = this.getNumberOfJobs() - 1;
        if (verbose) printState();
    }

    public synchronized Map<String, Job> getIdToJob() {
        return idToJob;
    }

    public synchronized void updateTable(InetAddress executor, Integer n){
        this.executorToInfos.get(executor).first = n;
    }

    private class CallbackThread extends Thread {
        @Override
        public void run() {
            while (true){
                try {
                    Pair<String, Object> p = executorCompletionService.take().get();
                    idToActiveJobs.remove(p.first);
                    if (idToJob.get(p.first).getStatus() == JobStatus.ABORTED) {
                        idToJob.remove(p.first);
                        Logger.log(LoggerPriority.NOTIFICATION, "Process with id: " + p.first + " has been aborted");
                        continue;
                    }
                    Logger.log(LoggerPriority.NOTIFICATION, "Process (with id " + p.first + " finished with code): " + JobReturnValue.OK);
                    ((LazyHashMap)idToJob).updateOnFile(p.first);         //black magic
                    decrementJobs(false);
                    printState();
                    UpdateTableMessage msg = new UpdateTableMessage(getNumberOfJobs(), idToJob.get(p.first).getID());
                    Broadcaster.getInstance().send(msg);
                    //MulticastPublisher.send(msg);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void runUncompletedJobs(){
        ArrayList <Job> uncompletedJobs = new ArrayList<>();
        Logger.log(LoggerPriority.NOTIFICATION, "Recovering uncompleted jobs");
        File dir = new File(System.getProperty("user.home") + ExecutorMain.relativePathToArchiveDir);
        File[] directoryListing = dir.listFiles();
        Job loadedJob;
        Integer counter = 0;
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.getName().contains("PENDING") && !child.getName().equals("placeholder")){
                    counter++;
                    try {
                        FileInputStream fis = new FileInputStream(child);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        loadedJob = (Job)ois.readObject();
                    } catch (FileNotFoundException e){
                        continue;
                    } catch (IOException|ClassNotFoundException e) {
                        e.printStackTrace();
                        continue;
                    }
                    uncompletedJobs.add(loadedJob);
                }
            }
        }
        Logger.log(LoggerPriority.NOTIFICATION, "Found " + counter + " incompleted jobs");
        for (Job job : uncompletedJobs) {
            job.call();
            this.idToJob.put(job.getID(), job);
        }
        Logger.log(LoggerPriority.NOTIFICATION, "All uncompleted jobs have been executed");
    }

    public ArrayList<InetAddress> getKnownExecutors() {
        return knownExecutors;
    }

    public void saveKnownExecutors(){
        File file = new File(System.getProperty("user.home") + "/knownExecutors.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            for (InetAddress ia : knownExecutors){
                bw.write(ia.toString());
                bw.newLine();
            }
            bw.close();
            osw.close();
            fos.close();
        } catch (IOException e){
            Logger.log(LoggerPriority.ERROR, "(not fatal) Error while saving known executors, continuing");
        }
    }

    public void printState(){
        System.out.println("***************************");
        System.out.println(new PrettyPrintingMap<InetAddress, Pair<Integer, Integer>>(this.executorToInfos));
        System.out.println("***************************");
    }
}
