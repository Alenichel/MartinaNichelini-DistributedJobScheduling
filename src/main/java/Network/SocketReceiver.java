package Network;

import Enumeration.LoggerPriority;
import Enumeration.MessageType;
import Enumeration.SocketReceiverType;
import Messages.Message;
import main.executorMain;
import utils.CallbacksEngine;
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
                Logger.log(LoggerPriority.NOTIFICATION, "SR -> Received new message");
                Logger.log(LoggerPriority.NOTIFICATION, "Received message of type: " +
                        ((Message)rcv).getType().toString() +
                        " by " + socket.getRemoteSocketAddress());
                CallbacksEngine.getIstance().handleCallback(rcv, socket.getInetAddress());
                socket.close();
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            Logger.log(LoggerPriority.ERROR, e.toString());
            e.printStackTrace();
        }
    }


}