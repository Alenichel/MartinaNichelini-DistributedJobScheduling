package Interfaces;

import Enumeration.LoggerPriority;
import utils.Logger;

public class VeryComplexJobExecutor implements JobExecutor {

    private Integer timer = (int)(Math.random() * 100);

    @Override
    public void execute() throws InterruptedException {
        Logger.log(LoggerPriority.NOTIFICATION, "Job started");
        Thread.sleep(timer);
        Logger.log(LoggerPriority.NOTIFICATION, "Job completed");
    }
}
