package Messages;

import Entities.Job;
import Enumeration.MessageType;

public class ProposeJobMessage extends Message {

    private Job job;

    public ProposeJobMessage(Job job) {
        super(MessageType.PROPOSE_JOB);
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
