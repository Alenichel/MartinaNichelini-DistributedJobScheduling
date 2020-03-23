package Messages;

import Entities.Job;
import Enumeration.MessageType;

public class ProposeJobMessage extends Message {

    private Job job;

    public ProposeJobMessage(Job job) {
        this.type = MessageType.PROPOSE_JOB;
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
