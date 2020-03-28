package Tasks;

import Enumeration.JobReturnValue;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Thread.sleep;


public class Sleep implements Task<JobReturnValue>, Serializable {

    private static final long serialVersionUID = 227L;

    private JobReturnValue sleepTask(){
        Integer timer = ThreadLocalRandom.current().nextInt(1, 10 + 1);
        try {
            sleep(timer);
            return JobReturnValue.OK;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return JobReturnValue.KO;
        }
    }

    public JobReturnValue execute() {
        return sleepTask();
    }

    @Override
    public String toString(){
        return "SleepTask";
    }
}