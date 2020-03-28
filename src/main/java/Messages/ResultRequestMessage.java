package Messages;

import Enumeration.MessageType;

public class ResultRequestMessage extends Message {
    private String jobId;

    public ResultRequestMessage(String jobId) {
        this.type = MessageType.RESULT_REQUEST_MESSAGE;
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }
}
