package Entities;

import Enumeration.JobType;
import Enumeration.LoggerPriority;
import Interfaces.JobExecutor;
import Interfaces.VeryComplexJobExecutor;
import utils.Logger;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

public class Job implements Serializable {
    private Boolean isAssigned;
    private InetAddress executorAddress;
    private JobType type;
    private String id;
    private Boolean isCompleted;
    private JobExecutor je;

    public Job(JobType type) {
        this.isAssigned = false;
        this.isCompleted = false;
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

    public String getId() {
        return id;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public JobExecutor getJe() {
        return je;
    }
}
