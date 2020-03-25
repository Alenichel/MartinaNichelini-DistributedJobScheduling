package Interfaces;

import Entities.Executor;
import Enumeration.JobReturnValue;
import Enumeration.JobStatus;
import Enumeration.LoggerPriority;

import utils.Logger;
import utils.Pair;

import java.util.concurrent.ThreadLocalRandom;

public class VeryComplexJobExecutor extends JobExecutor {

    private Integer timer = ThreadLocalRandom.current().nextInt(1, 10 + 1);;

    public VeryComplexJobExecutor(String jobID) {
        super(jobID);
    }

    @Override
    public Object call() {
        Logger.log(LoggerPriority.WARNING, "Job started: (" + timer + "s)");
        Executor.getIstance().getIdToJob().get(this.jobID).setStatus(JobStatus.EXECUTION);
        try {
            Thread.sleep(timer * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Pair<String, JobReturnValue>(this.jobID, JobReturnValue.KO);
        }
        return new Pair<String, JobReturnValue>(this.jobID, JobReturnValue.OK);
    }
}
