package Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SocketDatagramReceiver {


    public static void main(String[] args) throws UnknownHostException, IOException {
        byte[] buf = new byte[1000];
        DatagramPacket dgp = new DatagramPacket(buf, buf.length);
        DatagramSocket socket = new DatagramSocket(9670, InetAddress.getByName("0.0.0.0"));
        while(true) {
            System.out.println("Waiting for data");
            socket.receive(dgp);
            System.out.println("Data received");
            String rcvd = new String(dgp.getData(), 0, dgp.getLength()) + ", from address: "
                    + dgp.getAddress() + ", port: " + dgp.getPort();
            System.out.println(rcvd);
        }
    }
}
