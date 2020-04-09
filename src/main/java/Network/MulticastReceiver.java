package Network;

import Enumeration.LoggerPriority;
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

    public Integer listeningPort;
    private MulticastSocket socket = null;

    public MulticastReceiver(Integer listeningPort){
        this.listeningPort = listeningPort;
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[1000];
            DatagramPacket dgp = new DatagramPacket(buf, buf.length);

            this.socket = new MulticastSocket(this.listeningPort);
            socket.setNetworkInterface(NetworkInterface.getByName("en8"));
            InetAddress group = InetAddress.getByName("228.5.6.7");
            socket.joinGroup(group);

            Logger.log(LoggerPriority.NOTIFICATION,"MULTICAST_RECEIVER-> Waiting for data");
            while (true) {
                this.socket.receive(dgp);
                String local = NetworkUtilis.getLocalAddress().getHostAddress();
                String gotAddress = dgp.getAddress().getHostAddress();
                Logger.log(LoggerPriority.DEBUG,"DGR -> Data received");
                ByteArrayInputStream bis = new ByteArrayInputStream(dgp.getData());
                ObjectInputStream ois = new ObjectInputStream(bis);
                Message msg = (Message) ois.readObject();
                String rcvd = "DGR -> Receive message of type: " + msg.getType() + " from address: " +  dgp.getAddress();
                Logger.log(LoggerPriority.NOTIFICATION, rcvd);
                //CallbacksEngine.getIstance().handleCallback(msg, dgp.getAddress());
                }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
