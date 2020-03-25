package Entities;

import Enumeration.JobStatus;
import Enumeration.JobType;
import Enumeration.LoggerPriority;
import Interfaces.JobExecutor;
import Interfaces.VeryComplexJobExecutor;
import utils.Logger;

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

    public Job(JobType type) {
        this.isAssigned = false;
        this.status = JobStatus.UNASSIGNED;
        this.type = type;
        this.id = UUID.randomUUID().toString();

        switch (this.type){
            case VERY_COMPLEX_JOB:
                je = new VeryComplexJobExecutor();
                break;
            default:
                Logger.log(LoggerPriority.ERROR, "Job type error");
                break;
        }
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

    public JobType getType() {
        return type;
    }

    public String getID() {
        return id;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) { this.status = status;}

    @Override
    public void run() {
        try {
            je.execute();
            Logger.log(LoggerPriority.NOTIFICATION, "EXECUTOR_THREAD: job with id " + id+ " correctly executed");
            Executor e = Executor.getIstance();
            e.jobCompleted(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
