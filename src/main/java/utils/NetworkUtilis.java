package utils;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;
import Enumeration.LoggerPriority;
import Messages.JoinMessage;
import Network.SocketReceiver;
import Network.SocketSenderUnicast;


public class NetworkUtilis {

    public static InetAddress getLocalAddress() {
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (((Enumeration) e).hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    String sub = i.getHostAddress().toString().substring(0, 3);
                    if ("192".equals(sub)) {
                        return InetAddress.getByName(i.getHostAddress());
                    }
                }
            }
            return InetAddress.getLocalHost();
        } catch (UnknownHostException | SocketException e) {
            Logger.log(LoggerPriority.ERROR, "It was impossible to retrieve the address");
            return null;
        }
    }

    public static InetAddress getExternalAddress() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            String ip = in.readLine(); //you get the IP as a String
            return InetAddress.getByName(ip);
        } catch (Exception e) {
            return null;
        }
    }

    /*public static Boolean checkPortOpeness(Integer portNumber) {
        try {
            InetAddress ia = getExternalAddress();
            SocketSenderUnicast.send(new JoinMessage(), ia, portNumber);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }*/
}
