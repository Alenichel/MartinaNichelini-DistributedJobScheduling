package Entities;

import Enumeration.JobStatus;
import Enumeration.JobType;
import Tasks.Task;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

public class Job extends Thread implements Serializable {
    private Boolean isAssigned;
    private InetAddress executorAddress;
    private JobType type;
    private String id;
    private JobStatus status;
    private JobExecutor je;

    /*public Job(JobType type) {
        this.isAssigned = false;
        this.status = JobStatus.UNASSIGNED;
        this.type = type;
        this.id = UUID.randomUUID().toString();

        switch (this.type){
            case VERY_COMPLEX_JOB:
                je = new VeryComplexJobExecutor(this.id);//TODO
                break;
            default:
                Logger.log(LoggerPriority.ERROR, "Job type error");
                break;
        }
    }*/

    public Job(Task task){
        this.isAssigned = false;
        this.status = JobStatus.UNASSIGNED;
        this.type = type;
        this.id = UUID.randomUUID().toString();

        this.je = new JobExecutor(this.id, task);
    }

    public void setExecutor(InetAddress executorAddress) {
        this.isAssigned = true;
        this.executorAddress = executorAddress;
    }

    public InetAddress getExecutorAddress() {
        return executorAddress;
    }

    public Boolean getAssigned() {
        return isAssigned;
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

    public JobExecutor getJe() {
        return je;
    }
}
