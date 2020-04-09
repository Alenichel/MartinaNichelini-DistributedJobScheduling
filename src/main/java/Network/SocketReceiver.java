package Network;

import Enumeration.LoggerPriority;
import Enumeration.SocketReceiverType;
import Main.ExecutorMain;
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
            this.port = ExecutorMain.clientsPort;
        }
        if (stype.equals(SocketReceiverType.TO_EXECUTOR)){
            this.port = ExecutorMain.executorsPort;
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

                OutputStream os = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                InputStream input = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(input);


                Object rcv = ois.readObject();
                Logger.log(LoggerPriority.DEBUG, "SR -> Received new message");
                CallbacksEngine.getIstance().handleCallback(rcv, socket.getInetAddress(), oos);
                socket.close();
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            Logger.log(LoggerPriority.ERROR, e.toString());
            e.printStackTrace();
        }
    }


}