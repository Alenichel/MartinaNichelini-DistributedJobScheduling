package Messages;

import Enumeration.MessageType;

public class PongMessage extends Message {

    private Integer numberOfJobs;

    public PongMessage(MessageType type, Integer numberOfJobs) {
        super(MessageType.PONG_MESSAGE.PONG_MESSAGE);
        this.numberOfJobs = numberOfJobs;
    }

    public Integer getNumberOfJobs() {
        return numberOfJobs;
    }
}
