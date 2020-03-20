package Entities;

import utils.PrettyPrintingMap;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Executor {
    private InetAddress address;
    private Integer numberOfJobs;

    private Map<InetAddress, Integer> executorToJobs = new HashMap<InetAddress, Integer>();

    public Executor() {
        this.numberOfJobs = 0;
    }

    /*public Map<InetAddress, Integer> getExecutorToJobs() {
        return executorToJobs;
    }*/

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

}
