package Network;

import Enumeration.LoggerPriority;
import Enumeration.MessageType;
import Enumeration.SocketReceiverType;
import Messages.Message;
import main.executorMain;
import utils.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketReceiver extends Thread {

    private Integer port;
    private SocketReceiverType stype;
    private ServerSocket ssocket;

    public SocketReceiver(SocketReceiverType type){
        this.stype = type;
        if (stype.equals(SocketReceiverType.TO_CLIENT)){
            this.port = executorMain.clientsPort;
        }
        if (stype.equals(SocketReceiverType.TO_EXECUTOR)){
            this.port = executorMain.executorsPort;
        }
    }

    public ServerSocket getSsocket() {
        return ssocket;
    }

    @Override
    public void run() {
        try {
            ssocket = new ServerSocket(port);
            while (true) {
                Socket socket = ssocket.accept();
                InputStream input = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(input);
                Object rcv = ois.readObject();
                MessageType msgType = ((Message)rcv).getType();
                Logger.log(LoggerPriority.NOTIFICATION, "Received message of type " + msgType.toString());
               /*BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line = reader.readLine();
                Logger.log(LoggerPriority.NOTIFICATION, "SR" + "(" + stype.toString() + ") "+ "-> " + line);*/
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            Logger.log(LoggerPriority.ERROR, e.toString());
        }
    }


}