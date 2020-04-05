package Messages;

import Enumeration.MessageType;

public class ResultRequestMessage extends Message {
    private String jobId;
    private Boolean isForwarded;

    public ResultRequestMessage(String jobId, Boolean isForwarded) {
        this.type = MessageType.RESULT_REQUEST_MESSAGE;
        this.jobId = jobId;
        this.isForwarded = isForwarded;
    }

    public String getJobId() {
        return jobId;
    }

    public Boolean getForwared() {
        return isForwarded;
    }
}
