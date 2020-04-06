package Network;

import Main.ExecutorMain;
import Messages.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastPublisher {

    public static void send(Message msg) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName("230.0.0.0");
        byte[] buf;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        oos.flush();
        buf = bos.toByteArray();

        DatagramPacket packet  = new DatagramPacket(buf, buf.length, group, ExecutorMain.multicastPort);
        socket.send(packet);
        socket.close();
    }
}