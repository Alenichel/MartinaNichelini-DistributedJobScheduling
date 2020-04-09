package Messages;

import Enumeration.MessageType;

public class UpdateTableMessage extends Message{

    private Integer nJobs;
    private String jobId;

    public UpdateTableMessage(Integer nJobs, String jobId){
        this.type = MessageType.UPDATE_TABLE_MESSAGE;
        this.nJobs = nJobs;
        this.jobId = jobId;

    }

    public Integer getnJobs() {
        return nJobs;
    }

    public String getJobId() { return jobId; }
}
