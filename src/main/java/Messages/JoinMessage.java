package Messages;

import Enumeration.MessageType;

public class JoinMessage extends Message {
    private Boolean justExploring;

    public JoinMessage() {
        this.type = MessageType.JOIN_MESSAGE;
        this.justExploring = false;
    }

    public Boolean getJustExploring() {
        return justExploring;
    }

    public void setJustExploring(Boolean justExploring) {
        this.justExploring = justExploring;
    }
}
