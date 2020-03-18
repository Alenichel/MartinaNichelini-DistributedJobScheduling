package Network;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketSenderUnicast {

    private Integer socketPort;
    private String host;

    public SocketSenderUnicast(Integer socketPort, String host) {
        this.socketPort = socketPort;
        this.host = host;
    }

    public void send(String message) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(this.host, this.socketPort);
        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        writer.println(message);
    }
}
