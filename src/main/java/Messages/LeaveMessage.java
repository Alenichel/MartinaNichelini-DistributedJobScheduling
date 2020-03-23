package Messages;

import Enumeration.MessageType;

public class LeaveMessage extends Message {
    public LeaveMessage() {
        this.type = MessageType.LEAVE_MESSAGE;
    }
}
