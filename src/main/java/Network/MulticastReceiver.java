package Network;

import Entities.Executor;
import Enumeration.LoggerPriority;
import Main.ExecutorMain;
import Messages.Message;
import utils.CallbacksEngine;
import utils.Logger;
import utils.NetworkUtilis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[1000];

    public void run() {
        try {
            socket = new MulticastSocket(ExecutorMain.multicastPort);
            socket.setNetworkInterface(NetworkInterface.getByName("en8"));
            InetAddress group = InetAddress.getByName("228.5.6.7");
            socket.joinGroup(group);
            Logger.log(LoggerPriority.DEBUG,"MULTICAST_RECEIVER-> Waiting for data");
            while (true) {
                DatagramPacket dgp = new DatagramPacket(buf, buf.length);
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
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
