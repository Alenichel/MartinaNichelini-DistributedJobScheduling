package Messages;

import Enumeration.MessageType;
import java.io.Serializable;

public abstract class Message implements Serializable {

    protected MessageType type;

    public Message(){
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
