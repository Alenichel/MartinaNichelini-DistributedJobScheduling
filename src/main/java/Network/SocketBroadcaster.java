package Network;

import Enumeration.MessageType;
import Messages.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

public class SocketBroadcaster {
    public static void send(Integer port, Message msg) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] buf;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        oos.flush();
        buf = bos.toByteArray();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("255.255.255.255"), port);
        socket.send(packet);
    }
}
