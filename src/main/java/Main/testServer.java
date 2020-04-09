package Main;

import Network.MulticastReceiver;

public class testServer {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        MulticastReceiver mr = new MulticastReceiver(ExecutorMain.multicastPort);
        mr.run();
    }
}
