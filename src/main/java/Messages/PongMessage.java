package Messages;

import Enumeration.MessageType;

public class PongMessage extends Message {

    private Integer numberOfJobs;

    public PongMessage(Integer numberOfJobs) {
        this.type = MessageType.PONG_MESSAGE;
        this.numberOfJobs = numberOfJobs;
    }

    public Integer getNumberOfJobs() {
        return numberOfJobs;
    }
}
