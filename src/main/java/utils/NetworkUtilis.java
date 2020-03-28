package utils;



import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import Enumeration.LoggerPriority;


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
}
