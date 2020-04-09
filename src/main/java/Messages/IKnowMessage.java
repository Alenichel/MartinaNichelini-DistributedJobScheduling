package Messages;

import Enumeration.JobStatus;
import Enumeration.MessageType;
import java.net.InetAddress;

public class IKnowMessage extends ResultResponseMessage {

    private JobStatus jobStatus;
    private Object result;
    private InetAddress actualOwner;

    public IKnowMessage(JobStatus jobStatus, Object result ) {
        this.type = MessageType.RESULT_RESPONSE_MESSAGE;
        this.jobStatus = jobStatus;
        this.result = result;
    }

    public IKnowMessage(InetAddress actualOwner) {
        this.actualOwner = actualOwner;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public Object getResult() {
        return result;
    }

    public InetAddress getActualOwner() {
        return actualOwner;
    }
}
