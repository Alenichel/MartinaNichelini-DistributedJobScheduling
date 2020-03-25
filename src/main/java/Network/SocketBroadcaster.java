package Network;

import Enumeration.MessageType;
import Messages.Message;

import java.io.IOException;
import java.net.*;

public class SocketBroadcaster {
    public static void send(Integer port, Message msg) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] buf = new byte[1000];
        buf = msg.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("255.255.255.255"), port);
        socket.send(packet);
    }
}
