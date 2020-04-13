package Network;

import Main.ExecutorMain;
import Messages.JoinMessage;
import Messages.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

public class SocketDatagramBroadcaster implements BroadcastingUnit {

    private Integer port;

    public SocketDatagramBroadcaster(Integer port) {
        this.port = port;
    }

    public void sayHello(){
        Message msg = new JoinMessage(ExecutorMain.nThreads);
        this.send(msg);
    }

    public void send(Message msg) {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] buf;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(msg);
            oos.flush();
            buf = bos.toByteArray();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("255.255.255.255"), this.port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
