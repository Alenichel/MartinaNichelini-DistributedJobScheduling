package Messages;

import Enumeration.MessageType;

public class DumbMessage extends Message{
    public  DumbMessage(){
        this.type = MessageType.DUMB_MESSAGE;
    }
}
