package Messages;

import Enumeration.MessageType;

public class JoinMessage extends Message {
    private Boolean justExploring;
    private Integer nThreads;

    public JoinMessage(Integer nThread) {
        this.type = MessageType.JOIN_MESSAGE;
        this.justExploring = false;
        this.nThreads = nThread;
    }

    public Boolean getJustExploring() {
        return justExploring;
    }

    public void setJustExploring(Boolean justExploring) {
        this.justExploring = justExploring;
    }

    public Integer getNThreads(){ return this.nThreads;}
}
