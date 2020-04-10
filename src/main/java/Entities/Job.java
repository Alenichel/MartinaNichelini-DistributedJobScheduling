package Entities;

import Enumeration.JobStatus;
import Tasks.Task;
import utils.Pair;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.Callable;

public class Job extends Thread implements Serializable, Callable {
    private Boolean isAssigned;
    private InetAddress executorAddress;
    private String id;
    private JobStatus status;
    private Object result;
    private Task task;

    public Job(Task task){
        this.isAssigned = false;
        this.status = JobStatus.UNASSIGNED;
        this.id = UUID.randomUUID().toString();

        this.task = task;
    }

    public String getType() {
        return this.task.toString();
    }

    public String getID() {
        return id;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) { this.status = status;}

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public Object call() {
        this.status = JobStatus.EXECUTION;
        Object returned = this.task.execute();
        this.status = JobStatus.COMPLETED;
        this.result = returned;
        return new Pair<String, Object>(this.id, returned);
    }
}
