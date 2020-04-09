package Messages;

import Enumeration.MessageType;

import java.net.InetAddress;

public class FallenExecutor extends Message {

    private InetAddress lostExecutorAddress;

    public FallenExecutor(InetAddress lostOne){
        this.type = MessageType.FALLEN_EXECUTOR;
        this.lostExecutorAddress = lostOne;
    }

    public InetAddress getLostExecutorAddress() {
        return lostExecutorAddress;
    }
}
