import Network.SocketClient;

import java.io.IOException;

public class clientMain {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        SocketClient sc = new SocketClient("127.0.0.1", 9669);
    }
}
