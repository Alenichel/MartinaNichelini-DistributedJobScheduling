package Entities;

import Enumeration.JobType;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

public class Job implements Serializable {
    private Boolean isAssigned;
    private InetAddress executorAddress;
    private JobType type;
    private String id;
    private Boolean isCompleted;

    public Job(JobType type) {
        this.isAssigned = false;
        this.isCompleted = false;
        this.type = type;
        this.id = UUID.randomUUID().toString();
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
}
