package Network;

import Enumeration.LoggerPriority;
import Enumeration.SocketReceiverType;
import main.executorMain;
import utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line = reader.readLine();
                Logger.log(LoggerPriority.NOTIFICATION, "SR" + "(" + stype.toString() + ") "+ "-> " + line);
                socket.close();
            }
        } catch (IOException e) {
            Logger.log(LoggerPriority.ERROR, e.toString());
        }
    }


}