package Entities;

import Enumeration.JobReturnValue;
import Enumeration.JobStatus;
import Enumeration.LoggerPriority;
import Interfaces.JobExecutor;

import utils.Logger;
import utils.NetworkUtilis;
import utils.Pair;
import utils.PrettyPrintingMap;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;


public class Executor {
    private InetAddress address;
    private Integer numberOfJobs;
    private Map<String, Job> idToJob;
    private Map<InetAddress, Integer> executorToJobs;

    private java.util.concurrent.Executor executorService;
    private CompletionService<Pair<String, JobReturnValue>> executorCompletionService;
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

        this.numberOfJobs = 0;
        this.executorToJobs = new HashMap<InetAddress, Integer>();
        this.idToJob = new HashMap<String, Job>();
        try {
            this.address = NetworkUtilis.getLocalAddress();
            this.executorToJobs.put(this.address, 0);
        } catch (UnknownHostException | SocketException e) {
            Logger.log(LoggerPriority.ERROR, "Unknown host encountered during initialization, continuing...");
        }

        this.executorService = Executors.newFixedThreadPool(1);
        this.executorCompletionService = new ExecutorCompletionService<>(executorService);
        this.ct = new CallbackThread();
        this.ct.start();
    }

    public synchronized void addExecutor(InetAddress address, Integer jobs){
        if (!this.executorToJobs.containsKey(address)) {
            this.executorToJobs.put(address, jobs);
        }
        printState();
    }

    public synchronized void removeExecutor(InetAddress addres){
        executorToJobs.remove(addres);
        System.out.println("***************************");
        System.out.println(new PrettyPrintingMap<InetAddress, Integer>(this.executorToJobs));
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
            if(value < minValue && !key.equals(this.address)) {
                minValue = value;
                minKey = key;
            }
        }
        return minKey;
    }

    public InetAddress proposeJob(){
        return getMinKey(this.executorToJobs);
    }

    public void acceptJob(Job job) throws InterruptedException {
        Logger.log(LoggerPriority.NOTIFICATION, "EXECUTOR: Adding job of type " + job.getType() + " added to the job queue (id: " + job.getID() + ")");

        executorCompletionService.submit(job.getJe());
        job.setStatus(JobStatus.PENDING);
        this.idToJob.put(job.getID(), job);
    }

    public void jobCompleted(String id){
        this.idToJob.get(id).setStatus(JobStatus.COMPLETED);
        this.numberOfJobs--;
        this.printState();
    }

    public Integer getNumberOfJobs() {
        return numberOfJobs;
    }

    private class CallbackThread extends Thread {
        private ArrayList<Job> executionQueue = new ArrayList<Job>();

        @Override
        public void run() {
            while (true){
                try {
                    Pair<String, JobReturnValue> p = executorCompletionService.take().get();
                    Logger.log(LoggerPriority.NOTIFICATION, "Process with id: " + p.first + " finished with code: " + p.second);
                    idToJob.get(p.first).setStatus(JobStatus.COMPLETED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void printState(){
        System.out.println("***************************");
        System.out.println(new PrettyPrintingMap<InetAddress, Integer>(this.executorToJobs));
        System.out.println("***************************");
    }

}
