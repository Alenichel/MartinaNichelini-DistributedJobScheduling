package Entities;

import Enumeration.JobReturnValue;
import Tasks.Task;
import utils.Pair;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class JobExecutor implements Serializable, Callable {

    private String jobID;
    private Task task;

    public JobExecutor(String jobID, Task task){
        this.jobID = jobID;
        this.task = task;
    }

    @Override
    public Object call() {
        Object returned = this.task.execute();
        return new Pair<String, Object>(this.jobID, returned);
        //return new Pair<String, JobReturnValue>(this.jobID, JobReturnValue.OK);
    }

    @Override
    public String toString(){
        return task.toString();
    }
}
