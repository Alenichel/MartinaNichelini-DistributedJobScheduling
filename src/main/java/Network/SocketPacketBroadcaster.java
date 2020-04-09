package Network;

import Messages.Message;

public class SocketPacketBroadcaster implements BroadcastingUnit{

    private Integer port;

    public SocketPacketBroadcaster(Integer port) {
        this.port = port;
    }

    public void sayHello(){

    }

    public void send(Message msg){

    }
}
