package Network;

import Enumeration.LoggerPriority;
import Enumeration.LoggerType;
import utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {

    private ServerSocket ssocket;

    @Override
    public void run() {
        try {
            ssocket = new ServerSocket(1717);
            Logger.log(LoggerType.SERVER_SIDE, LoggerPriority.NORMAL, "Listening");
            while (true) {
                Socket socket = this.ssocket.accept();
                Logger.log(LoggerType.SERVER_SIDE, LoggerPriority.NORMAL,"[*] NOTIFICATION: new client connected");
                //SocketServerImplementation virtualClientThread;
                //virtualClientThread = new SocketServerImplementation(socket);
                //virtualClientThread.start();
            }
        } catch (IOException e){
            assert false;
        }
    }
}