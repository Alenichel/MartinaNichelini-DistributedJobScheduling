package Network;


import Enumeration.LoggerPriority;
import Messages.Message;
import utils.CallbacksEngine;
import utils.Logger;
import utils.NetworkUtilis;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SocketDatagramReceiver  extends Thread  {

    public Integer listeningPort;
    private DatagramSocket socket;

    public SocketDatagramReceiver(Integer listeningPort) {
        this.listeningPort = listeningPort;
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[1000];
            DatagramPacket dgp = new DatagramPacket(buf, buf.length);
            this.socket = new DatagramSocket(this.listeningPort, InetAddress.getByName("0.0.0.0"));
            Logger.log(LoggerPriority.DEBUG,"DGR -> Waiting for data");
            while (true) {
                this.socket.receive(dgp);
                String local = NetworkUtilis.getLocalAddress().getHostAddress();
                String gotAddress = dgp.getAddress().getHostAddress();
                if ( ! local.equals(gotAddress) ){
                    Logger.log(LoggerPriority.DEBUG,"DGR -> Data received");
                    ByteArrayInputStream bis = new ByteArrayInputStream(dgp.getData());
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    Message msg = (Message) ois.readObject();
                    String rcvd = "DGR -> Receive message of type: " + msg.getType() + " from address: " +  dgp.getAddress();
                    Logger.log(LoggerPriority.DEBUG, rcvd);
                    CallbacksEngine.getIstance().handleCallback(msg, dgp.getAddress());
                }
            }
        } catch (Exception e) {
            Logger.log(LoggerPriority.ERROR, e.toString());
            e.printStackTrace();
        }
    }
}
