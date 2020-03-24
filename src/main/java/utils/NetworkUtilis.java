package utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class NetworkUtilis {

    public static void getLocalAddress() throws SocketException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while(((Enumeration) e).hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                System.out.println(i.getHostAddress());
            }
        }
    }
}
