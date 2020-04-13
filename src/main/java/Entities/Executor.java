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
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

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
        this.executorToInfos = new HashMap<InetAddress, Pair<Integer, Integer>>();
        this.executorToInfos.put(ExecutorMain.localIP, new Pair<>(0, ExecutorMain.nThreads));
        this.foreignCompletedJobs = new HashMap<String, InetAddress>();
        this.executorService = Executors.newFixedThreadPool(ExecutorMain.nThreads);
        this.executorCompletionService = new ExecutorCompletionService<>(executorService);
        this.idToJob = new LazyHashMap<String, Job>(ExecutorMain.relativePathToArchiveDir);
        this.idToActiveJobs = new HashMap<>();
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
        printState();
    }

    public synchronized void removeExecutor(InetAddress addres){
        executorToInfos.remove(addres);
        System.out.println("***************************");
        System.out.println(new PrettyPrintingMap<InetAddress, Pair<Integer, Integer>>(this.executorToInfos));
        System.out.println("***************************");
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

    public void acceptJob(Job job) {
        Logger.log(LoggerPriority.NOTIFICATION, "EXECUTOR: Adding job of type " + job.getType() + " added to the job queue (id: " + job.getID() + ")");

        job.setStatus(JobStatus.PENDING);
        this.idToJob.put(job.getID(), job);
        this.idToActiveJobs.put(job.getID(), job);
        executorCompletionService.submit(job);
        incrementJobs();

        UpdateTableMessage msg = new UpdateTableMessage(getNumberOfJobs(), job.getID());

        Broadcaster.getInstance().send(msg);
        //MulticastPublisher.send(msg);
    }

    public void reassignJob(InetAddress idleExecutor){
        if (this.getNumberOfJobs() > ExecutorMain.nThreads + 1){
            Job job = null;
            for (Job j : this.idToActiveJobs.values()){
                if(j.getStatus() == JobStatus.PENDING){
                    job = j;
                    break;
                }
            }
            try {
                ProposeJobMessage pjb = new ProposeJobMessage(job);
                SocketSenderUnicast.send(pjb, idleExecutor, ExecutorMain.executorsPort);
                job.setStatus(JobStatus.ABORTED);
                decrementJobs();
                Logger.log(LoggerPriority.NOTIFICATION, "Correctly reassigned job with id: " + job.getID());
            } catch (IOException | ClassNotFoundException e) {
                Logger.log(LoggerPriority.ERROR, "(not fatal) Impossible to handle job reassignment. Continuing");
            } catch (NullPointerException e){
                Logger.log(LoggerPriority.DEBUG, "no available jobs for reassignment");
                return;
            }

            UpdateTableMessage utm = new UpdateTableMessage(this.getNumberOfJobs(), job.getID());
            Broadcaster.getInstance().send(utm);
        }
    }

    public Map<InetAddress, Pair<Integer,Integer>> getExecutorToInfos() { return executorToInfos; }

    public Integer getNumberOfJobs() {
        return this.executorToInfos.get(ExecutorMain.localIP).first;
    }

    public Map<String, InetAddress> getForeignCompletedJobs() { return foreignCompletedJobs; }

    private void incrementJobs(){
        this.executorToInfos.get(ExecutorMain.localIP).first = this.getNumberOfJobs() + 1;
        printState();
    }

    private void decrementJobs(){
        this.executorToInfos.get(ExecutorMain.localIP).first = this.getNumberOfJobs() - 1;
        printState();
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
                    decrementJobs();
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
        File dir = new File(System.getProperty("user.dir") + ExecutorMain.relativePathToArchiveDir);
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
        File file = new File(System.getProperty("user.dir") + "/knownExecutors.txt");
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
