package Messages;

import Enumeration.JobStatus;
import Enumeration.MessageType;
import Enumeration.TaskType;

public class ResultResponseMessage extends Message {

    protected Boolean idk;

    public ResultResponseMessage() {
        this.type = MessageType.RESULT_RESPONSE_MESSAGE;
    }

    public Boolean getIdk() {
        return idk;
    }
}
