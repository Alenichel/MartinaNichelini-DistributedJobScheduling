package Interfaces;

import Enumeration.JobType;

import java.io.Serializable;

public abstract class JobExecutor implements Serializable {
    public void execute() throws InterruptedException {}
}
