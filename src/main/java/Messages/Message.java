package Messages;

import Enumeration.MessageType;

import java.io.Serializable;

public class Message implements Serializable {

    private MessageType type;

    public Message(MessageType type){
        this.type = type;
    }

    public MessageType getType(){
        return this.type;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }
}
