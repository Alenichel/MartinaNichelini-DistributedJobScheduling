package Interfaces;

import Enumeration.LoggerPriority;
import utils.Logger;

import java.io.Serializable;
import java.sql.Time;
import java.util.concurrent.ThreadLocalRandom;

public class VeryComplexJobExecutor extends JobExecutor {

    private Integer timer = ThreadLocalRandom.current().nextInt(1, 10 + 1);;

    @Override
    public void execute() throws InterruptedException {
        Logger.log(LoggerPriority.WARNING, "Job started: (" + timer + "s)");
        Thread.sleep(timer * 1000);
        //Logger.log(LoggerPriority.NOTIFICATION, "Job completed");
    }
}
