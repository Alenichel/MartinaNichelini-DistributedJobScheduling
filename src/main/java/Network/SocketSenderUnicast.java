package Network;

import Messages.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SocketSenderUnicast {

    private static ObjectOutputStream buildOOS(InetAddress host, Integer port) throws IOException {
        Socket socket = new Socket(host, port);
        OutputStream os  = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        return oos;
    }

    public static void send(Message message, InetAddress host, Integer port) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = buildOOS(host, port);
        oos.writeObject(message);
        oos.flush();
        oos.reset();
    }

}
