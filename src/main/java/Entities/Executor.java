package Entities;

import Enumeration.JobType;
import utils.PrettyPrintingMap;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class Executor {
    private InetAddress address;
    private Integer numberOfJobs;
    BlockingQueue jobs;
    private Map<InetAddress, Integer> executorToJobs = new HashMap<InetAddress, Integer>();

    public Executor() {
        this.numberOfJobs = 0;
        jobs = new SynchronousQueue();
    }

    public synchronized void addExecutor(InetAddress address, Integer jobs){
        if (!this.executorToJobs.containsKey(address)) {
            this.executorToJobs.put(address, jobs);
        }
        System.out.println(new PrettyPrintingMap<InetAddress, Integer>(this.executorToJobs));
    }

    public synchronized void removeExecutor(InetAddress addres){
        executorToJobs.remove(addres);
        System.out.println(new PrettyPrintingMap<InetAddress, Integer>(this.executorToJobs));
    }

    public void setAddress(InetAddress address) {
        this.address = address;
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
        return getMinKey(this.executorToJobs);
    }

    public void acceptJob(Job j) throws InterruptedException {
        this.numberOfJobs ++;
        this.jobs.put(j);
    }

    private class executorThread extends Thread{
        @Override
        public void run() {
            while (true){
                if(!jobs.isEmpty()){
                    try {
                        Job currentJob = (Job)jobs.take();
                        currentJob.getJe().execute();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
