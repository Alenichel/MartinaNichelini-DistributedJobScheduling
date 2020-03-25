package Entities;

import Enumeration.JobStatus;
import Enumeration.LoggerPriority;
import utils.CallbacksEngine;
import utils.Logger;
import utils.NetworkUtilis;
import utils.PrettyPrintingMap;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Stream;

public class Executor {
    private InetAddress address;
    private Integer maxNumberOfConcurrentJob = 2;
    private Integer numberOfJobs;
    private BlockingQueue<Job> jobs;
    private Map<String, Job> idToJob;
    private Map<InetAddress, Integer> executorToJobs;
    private ExecutorThread et;

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
        this.jobs = new SynchronousQueue<Job>();
        this.et = new ExecutorThread();
        this.et.start();
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
        this.jobs.offer(job);
        this.numberOfJobs ++;
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

    private class ExecutorThread extends Thread{
        private ArrayList<Job> executionQueue;
        @Override
        public void run() {
            Logger.log(LoggerPriority.NOTIFICATION, "EXECUTOR_THREAD: I'm up and running");
            while (true){
                try {
                    Job j = (Job)jobs.take();
                    //executionQueue.add(j);

                    Long activeJobs = idToJob.entrySet().stream().filter($set -> $set.getValue().getStatus().equals(JobStatus.EXECUTION)).count();
                    Logger.log(LoggerPriority.ERROR,   "" + activeJobs );
                    Logger.log(LoggerPriority.NOTIFICATION, "EXECUTOR_THREAD: new job taken from the queue with id: " + j.getID());
                    j.start();
                    j.setStatus(JobStatus.EXECUTION);
                } catch (InterruptedException e) {
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
