package Interfaces;

import Enumeration.LoggerPriority;
import utils.Logger;

import java.io.Serializable;

public class VeryComplexJobExecutor implements JobExecutor, Serializable {

    private Integer timer = (int)(Math.random() * 100);

    @Override
    public void execute() throws InterruptedException {
        Logger.log(LoggerPriority.NOTIFICATION, "Job started");
        Thread.sleep(timer);
        Logger.log(LoggerPriority.NOTIFICATION, "Job completed");
    }
}
