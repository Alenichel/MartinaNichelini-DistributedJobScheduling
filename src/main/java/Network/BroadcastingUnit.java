package Network;

import Messages.Message;

public interface BroadcastingUnit {
    public void sayHello();
    public void send(Message msg);
}
