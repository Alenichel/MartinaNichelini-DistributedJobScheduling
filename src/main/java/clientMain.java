import Network.SocketBroadcaster;
import Network.SocketClient;

import java.io.IOException;

public class clientMain {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        SocketBroadcaster sb = new SocketBroadcaster(9670);
        sb.send("Ciao man");
    }
}
