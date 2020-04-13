package Messages;

import Enumeration.MessageType;

import java.util.ArrayList;

public class UpdateTableMessage extends Message{

    private Integer nJobs;
    private ArrayList<String> jobsId;

    public UpdateTableMessage(Integer nJobs){
        this.type = MessageType.UPDATE_TABLE_MESSAGE;
        this.nJobs = nJobs;
        this.jobsId = null;
    }

    public UpdateTableMessage(Integer nJobs, String jobId){
        this.type = MessageType.UPDATE_TABLE_MESSAGE;
        this.nJobs = nJobs;
        this.jobsId.add(jobId);
    }

    public UpdateTableMessage(Integer nJobs, ArrayList<String> jobsId){
        this.type = MessageType.UPDATE_TABLE_MESSAGE;
        this.nJobs = nJobs;
        this.jobsId = jobsId;
    }

    public Integer getnJobs() {
        return nJobs;
    }

    public ArrayList<String> getJobId() { return jobsId; }
}
