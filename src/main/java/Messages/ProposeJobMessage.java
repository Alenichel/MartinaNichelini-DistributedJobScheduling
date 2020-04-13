package Messages;

import Entities.Job;
import Enumeration.MessageType;

import java.util.ArrayList;

public class ProposeJobMessage extends Message {

    private ArrayList<Job> jobs;

    public ProposeJobMessage(ArrayList<Job> jobs) {
        this.type = MessageType.PROPOSE_JOB;
        this.jobs = jobs;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }
}
