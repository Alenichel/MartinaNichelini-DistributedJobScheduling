package Network;

import Entities.Executor;
import Enumeration.BroadcastingType;
import Enumeration.LoggerPriority;
import Main.ExecutorMain;
import Messages.Message;
import utils.Logger;

public class Broadcaster {

    private BroadcastingType type;
    private BroadcastingUnit bu;
    public static Broadcaster instance = null;

    public static Broadcaster getInstance(BroadcastingType bt) {
        if(instance==null)
            synchronized(Executor.class) {
                if( instance == null )
                    instance = new Broadcaster(bt);
            }
        return instance;
    }

    public static Broadcaster getInstance(){
        if(instance==null)
            synchronized(Executor.class) {
                if( instance == null )
                    instance = new Broadcaster(null);
            }
        return instance;
    }

    private Broadcaster(BroadcastingType bt){
        if (bt == null){
            Logger.log(LoggerPriority.WARNING, "Broadcasting type not given. Automatically chosen: UDP_LOCAL");
            this.type = BroadcastingType.LOCAL_UDP;
        }
        this.type = bt;
        Logger.log(LoggerPriority.NOTIFICATION, "You selected broadcasting type: " + this.type);
        if (this.type == BroadcastingType.GLOBAL_TCP){
            this.bu = new SocketPacketBroadcaster(ExecutorMain.executorsPort);
        } else {
            this.bu = new SocketDatagramBroadcaster(ExecutorMain.executorsPort);
        }
    };

    public void sayHello(){
        bu.sayHello();
    }

    public void send(Message msg){
        bu.send(msg);
    }
}
