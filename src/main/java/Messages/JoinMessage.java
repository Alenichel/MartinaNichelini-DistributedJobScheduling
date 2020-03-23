package Messages;

import Enumeration.MessageType;

public class JoinMessage extends Message {
    public JoinMessage() {
        this.type = MessageType.JOIN_MESSAGE;
    }
}
