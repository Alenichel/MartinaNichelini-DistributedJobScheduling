package Entities;

import Enumeration.JobReturnValue;
import Enumeration.JobStatus;
import Enumeration.LoggerPriority;

import Messages.UpdateTableMessage;
import Network.SocketBroadcaster;
import Main.ExecutorMain;
import utils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;


public class Executor {
    private InetAddress address;
    private Map<String, Job> idToJob;                           // must be persistent
    private Map<InetAddress, Integer> executorToNumberOfJobs;

    private Map<String, InetAddress> foreignCompletedJobs;

    private java.util.concurrent.Executor executorService;
    private CompletionService<Pair<String, Object>> executorCompletionService;
    private CallbackThread ct;


    public static Executor instance = null;

    public static Executor getIstance() {
        if(instance==null)
            synchronized(Executor.class) {
                if( instance == null )
                    instance = new Executor();
            }
        return instance;
    }

    public Executor() {
        this.executorToNumberOfJobs = new HashMap<InetAddress, Integer>();
        this.idToJob = new LazyHashMap<String, Job>(ExecutorMain.pathToArchiveDir);
        this.address = NetworkUtilis.getLocalAddress();
        this.executorToNumberOfJobs.put(this.address, 0);
        this.foreignCompletedJobs = new HashMap<String, InetAddress>();
        this.executorService = Executors.newFixedThreadPool(2);
        this.executorCompletionService = new ExecutorCompletionService<>(executorService);
        this.ct = new CallbackThread();
        this.ct.start();
        //this.runUncompletedJobs();
    }

    public synchronized void addExecutor(InetAddress address, Integer jobs){
        if (!this.executorToNumberOfJobs.containsKey(address)) {
            this.executorToNumberOfJobs.put(address, jobs);
        }
        printState();
    }

    public synchronized void removeExecutor(InetAddress addres){
        executorToNumberOfJobs.remove(addres);
        System.out.println("***************************");
        System.out.println(new PrettyPrintingMap<InetAddress, Integer>(this.executorToNumberOfJobs));
        System.out.println("***************************");
    }

    public InetAddress getAddress() {
        return address;
    }

    private InetAddress getMinKey(Map<InetAddress, Integer> map) {
        InetAddress minKey = null;
        int minValue = Integer.MAX_VALUE;
        for(InetAddress key : map.keySet()) {
            int value = map.get(key);
            if(value < minValue) {
                minValue = value;
                minKey = key;
            }
        }
        return minKey;
    }

    public InetAddress proposeJob(){
        return getMinKey(this.executorToNumberOfJobs);
    }

    public void acceptJob(Job job) {
        Logger.log(LoggerPriority.NOTIFICATION, "EXECUTOR: Adding job of type " + job.getType() + " added to the job queue (id: " + job.getID() + ")");

        executorCompletionService.submit(job.getJobExecutor());
        job.setStatus(JobStatus.PENDING);
        this.idToJob.put(job.getID(), job);
        incrementJobs();

        UpdateTableMessage msg = new UpdateTableMessage(getNumberOfJobs(), job.getID());
        try {
            SocketBroadcaster.send(ExecutorMain.executorsPort, msg);
            //MulticastPublisher.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<InetAddress, Integer> getExecutorToNumberOfJobs() { return executorToNumberOfJobs; }

    public Integer getNumberOfJobs() {
        return this.executorToNumberOfJobs.get(this.address);
    }

    public Map<String, InetAddress> getForeignCompletedJobs() { return foreignCompletedJobs; }

    private void incrementJobs(){
        this.executorToNumberOfJobs.put(this.address, this.getNumberOfJobs() + 1);
        printState();
    }

    private void decrementJobs(){
        this.executorToNumberOfJobs.put(this.address, this.getNumberOfJobs() - 1);
        printState();
    }

    public synchronized Map<String, Job> getIdToJob() {
        return idToJob;
    }

    public synchronized void updateTable(InetAddress executor, Integer n){
        this.executorToNumberOfJobs.put(executor, n);
    }

    private class CallbackThread extends Thread {
        @Override
        public void run() {
            while (true){
                try {
                    Pair<String, Object> p = executorCompletionService.take().get();
                    Logger.log(LoggerPriority.NOTIFICATION, "Process (with id " + p.first + " finished with code): " + JobReturnValue.OK);
                    idToJob.get(p.first).setStatus(JobStatus.COMPLETED);
                    idToJob.get(p.first).setResult(p.second);
                    ((LazyHashMap)idToJob).updateOnFile(p.first);         //black magic
                    decrementJobs();
                    printState();
                    UpdateTableMessage msg = new UpdateTableMessage(getNumberOfJobs(), idToJob.get(p.first).getID());
                    SocketBroadcaster.send(ExecutorMain.executorsPort, msg);
                    //MulticastPublisher.send(msg);
                } catch (InterruptedException | ExecutionException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void printState(){
        System.out.println("***************************");
        System.out.println(new PrettyPrintingMap<InetAddress, Integer>(this.executorToNumberOfJobs));
        System.out.println("***************************");
    }

    private Job loadFromFile(String key) throws IOException, ClassNotFoundException {
        String path = "src/main/java/ser/";

        String filename = path + key;
        File file = new File(filename);
        file.mkdirs();
        file.createNewFile();
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Job value = (Job) ois.readObject();
        ois.close();
        fis.close();
        return value;
    }

    /*private void runUncompletedJobs(){
        String path = "src/main/java/ser/";

        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        Job loadedJob;
        ArrayList<File> toDelete = new ArrayList<>();
        ArrayList<Job> toAccept = new ArrayList<>();
        try {
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    if (child.getName().contains("PENDING_")) {
                        loadedJob = loadFromFile(child.getName());
                        toDelete.add(child);
                        toAccept.add(loadedJob);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        for (File f: toDelete){
            f.delete();
        }

        for (Job j: toAccept){
            acceptJob(j);
        }

    }*/

}
