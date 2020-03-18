package Network;

import java.io.IOException;
import java.net.*;

public class SocketBroadcaster {

    private Integer port;

    public SocketBroadcaster(Integer port){
        this.port = port;
    }

    public void send(String message) throws UnknownHostException, SocketException, IOException {
        DatagramSocket socket = new DatagramSocket();//this.port, InetAddress.getByName("255.255.255.255"));
        socket.setBroadcast(true);
        String msg = "Ciao sono un messaggio di broadcast";
        byte[] buf = new byte[1000];
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("255.255.255.255"), this.port);
        socket.send(packet);
    }
}
