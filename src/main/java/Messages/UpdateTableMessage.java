package Messages;

import Enumeration.MessageType;
import utils.Pair;

import java.util.ArrayList;

public class UpdateTableMessage extends Message{

    private Integer nJobs;

    public UpdateTableMessage(Integer nJobs){
        this.type = MessageType.UPDATE_TABLE_MESSAGE;
        this.nJobs = nJobs;
    }

    public Integer getnJobs() {
        return nJobs;
    }
}
