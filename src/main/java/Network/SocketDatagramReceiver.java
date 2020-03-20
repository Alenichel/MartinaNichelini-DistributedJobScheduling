package Network;

import Entities.Executor;
import Enumeration.LoggerPriority;
import Enumeration.MessageType;
import utils.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SocketDatagramReceiver  extends Thread  {

    public Integer listeningPort;
    private DatagramSocket socket;
    private Executor executor;

    public SocketDatagramReceiver(Integer listeningPort, Executor executor) {
        this.listeningPort = listeningPort;
        this.executor = executor;
    }

    private void callback(String msg, InetAddress fromAddress) {
        switch (msg){
            case "JOIN_MESSAGE":
                this.executor.addExecutor(fromAddress, 0);

            default:
                break;
        }
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[1000];
            DatagramPacket dgp = new DatagramPacket(buf, buf.length);
            this.socket = new DatagramSocket(this.listeningPort, InetAddress.getByName("0.0.0.0"));
            this.executor.setAddress(socket.getLocalAddress());
            Logger.log(LoggerPriority.NOTIFICATION,"DGR -> Waiting for data");
            while (true) {
                this.socket.receive(dgp);
                if ( ! socket.getLocalAddress().equals(dgp.getAddress())){
                    Logger.log(LoggerPriority.NOTIFICATION,"DGR -> Data received");
                    String content = new String(dgp.getData(), 0, dgp.getLength());
                    String rcvd = "DGR -> " + content + ", from address: " + dgp.getAddress() + ", port: " + dgp.getPort();
                    Logger.log(LoggerPriority.NOTIFICATION, rcvd);
                    callback(content, dgp.getAddress());
                }
            }
        } catch (Exception e) {
            Logger.log(LoggerPriority.ERROR, e.toString());
        }
    }
}
