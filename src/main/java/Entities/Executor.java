package Entities;

import utils.PrettyPrintingMap;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class Executor implements Serializable {
    private InetAddress address;
    private Integer numberOfJobs;
    private BlockingQueue jobs;
    private Map<InetAddress, Integer> executorToJobs = new HashMap<InetAddress, Integer>();
    transient private ExecutorThread et;

    public Executor() {
        this.numberOfJobs = 0;
        jobs = new SynchronousQueue();
        this.et = new ExecutorThread();
        this.et.start();
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

    public Integer getNumberOfJobs() {
        return numberOfJobs;
    }

    private class ExecutorThread extends Thread{
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
