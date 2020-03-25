package Entities;

import Enumeration.LoggerPriority;
import utils.Logger;
import utils.NetworkUtilis;
import utils.PrettyPrintingMap;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class Executor {
    private InetAddress address;
    private Integer numberOfJobs;
    private BlockingQueue<Job> jobs;
    private Map<InetAddress, Integer> executorToJobs;
    private ExecutorThread et;

    public Executor() {
        this.numberOfJobs = 0;
        this.executorToJobs = new HashMap<InetAddress, Integer>();
        try {
            this.address = NetworkUtilis.getLocalAddress();
            this.executorToJobs.put(this.address, 0);
        } catch (UnknownHostException | SocketException e) {
            Logger.log(LoggerPriority.ERROR, "Unknown host encountered during initialization, continuing...");
        }
        jobs = new SynchronousQueue<Job>();
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
        this.numberOfJobs ++;
        this.jobs.offer(job);
    }

    public Integer getNumberOfJobs() {
        return numberOfJobs;
    }

    private class ExecutorThread extends Thread{
        @Override
        public void run() {
            Logger.log(LoggerPriority.NOTIFICATION, "EXECUTOR_THREAD: I'm up and running");
            while (true){
                try {
                    Job currentJob = (Job)jobs.take();
                    Logger.log(LoggerPriority.NOTIFICATION, "EXECUTOR_THREAD: new job taken from the queue with id: " + currentJob.getID());
                    currentJob.start();
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
