package Interfaces;

import Enumeration.JobType;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public abstract class JobExecutor implements Serializable, Callable {

    protected String jobID;

    public JobExecutor(String jobID){
        this.jobID = jobID;
    }

    public void run() {}
}
