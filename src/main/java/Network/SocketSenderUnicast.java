package Network;

import Messages.Message;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class SocketSenderUnicast {

    private static ObjectOutputStream buildOOS(InetAddress host, Integer port) throws IOException {
        Socket socket = new Socket(host, port);
        socket.setSoTimeout(5*1000);
        OutputStream os  = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        return oos;
    }

    public static void send(Message message, InetAddress host, Integer port) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = buildOOS(host, port);
        oos.writeObject(message);
        oos.flush();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //oos.reset();
    }

    public static Message sendAndWaitResponse(Message message, InetAddress host, Integer port) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(host, port);
        socket.setSoTimeout(5*1000);
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        ObjectInputStream ois = new ObjectInputStream(is);

        oos.writeObject(message);
        Message rcvd = (Message)ois.readObject();
        //socket.close();

        return rcvd;
    }

}
