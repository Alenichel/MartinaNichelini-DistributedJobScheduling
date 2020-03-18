package Network;

import Enumeration.LoggerPriority;
import utils.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SocketDatagramReceiver  extends Thread  {

    public Integer listeningPort;

    public SocketDatagramReceiver(Integer listeningPort) {
        this.listeningPort = listeningPort;
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[1000];
            DatagramPacket dgp = new DatagramPacket(buf, buf.length);
            DatagramSocket socket = new DatagramSocket(this.listeningPort, InetAddress.getByName("0.0.0.0"));
            while (true) {
                Logger.log(LoggerPriority.NOTIFICATION,"DGR -> Waiting for data");
                socket.receive(dgp);
                if ( ! socket.getLocalAddress().equals(dgp.getAddress())){
                    Logger.log(LoggerPriority.NOTIFICATION,"DGR -> Data received");
                    String rcvd = "DGR -> " + new String(dgp.getData(), 0, dgp.getLength()) + ", from address: "
                            + dgp.getAddress() + ", port: " + dgp.getPort();
                    Logger.log(LoggerPriority.NOTIFICATION, rcvd);
                }
            }
        } catch (Exception e) {
            Logger.log(LoggerPriority.ERROR, e.toString());
        }
    }
}
