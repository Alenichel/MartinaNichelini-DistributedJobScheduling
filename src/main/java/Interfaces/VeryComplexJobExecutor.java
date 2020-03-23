package Interfaces;

import Enumeration.LoggerPriority;
import utils.Logger;

import java.io.Serializable;
import java.sql.Time;

public class VeryComplexJobExecutor extends JobExecutor {

    private Integer timer = (int)(Math.random() * 100);

    @Override
    public void execute() throws InterruptedException {
        Logger.log(LoggerPriority.NOTIFICATION, "Job started");
        //Thread.sleep(timer * 1000);
        Logger.log(LoggerPriority.NOTIFICATION, "Job completed");
    }
}
