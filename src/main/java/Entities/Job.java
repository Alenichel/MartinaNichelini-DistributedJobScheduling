package Entities;

import Enumeration.JobStatus;
import Tasks.Task;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

public class Job extends Thread implements Serializable {
    private Boolean isAssigned;
    private InetAddress executorAddress;
    private String id;
    private JobStatus status;
    private JobExecutor je;
    private Object result;

    public Job(Task task){
        this.isAssigned = false;
        this.status = JobStatus.UNASSIGNED;
        this.id = UUID.randomUUID().toString();

        this.je = new JobExecutor(this.id, task);
    }

    public String getType() {
        return this.je.toString();
    }

    public String getID() {
        return id;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) { this.status = status;}

    public JobExecutor getJobExecutor() {
        return je;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
