package Network;


import Entities.Executor;
import Enumeration.LoggerPriority;
import Enumeration.MessageType;
import Messages.Message;
import utils.CallbacksEngine;
import utils.Logger;
import main.executorMain;
import utils.NetworkUtilis;

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

    @Override
    public void run() {
        try {
            byte[] buf = new byte[1000];
            DatagramPacket dgp = new DatagramPacket(buf, buf.length);
            this.socket = new DatagramSocket(this.listeningPort, InetAddress.getByName("0.0.0.0"));
            Logger.log(LoggerPriority.NOTIFICATION,"DGR -> Waiting for data");
            while (true) {
                this.socket.receive(dgp);
                String local = NetworkUtilis.getLocalAddress().getHostAddress();
                String gotAddress = dgp.getAddress().getHostAddress();
                Logger.log(LoggerPriority.NOTIFICATION, "My address is: " + local);
                if ( ! local.equals(gotAddress) ){
                    Logger.log(LoggerPriority.NOTIFICATION,"DGR -> Data received");
                    String content = new String(dgp.getData(), 0, dgp.getLength());
                    String rcvd = "DGR -> " + content + ", from address: " + dgp.getAddress() + ", port: " + dgp.getPort();
                    Logger.log(LoggerPriority.NOTIFICATION, rcvd);
                    CallbacksEngine.getIstance().handleCallback(content, dgp.getAddress());
                }
            }
        } catch (Exception e) {
            Logger.log(LoggerPriority.ERROR, e.toString());
        }
    }
}
