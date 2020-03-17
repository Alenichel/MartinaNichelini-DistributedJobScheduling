package Network;

import Enumeration.LoggerPriority;
import utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketManager extends Thread {

    private ServerSocket ssocket;
    private Integer serverPort = 9669;

    @Override
    public void run() {
        try {
            ssocket = new ServerSocket(serverPort);
            Logger.log(LoggerPriority.NORMAL, "Listening");
            while (true) {
                Socket socket = this.ssocket.accept();
                Logger.log(LoggerPriority.NOTIFICATION,"new client connected");
                //SocketServerImplementation virtualClientThread;
                //virtualClientThread = new SocketServerImplementation(socket);
                //virtualClientThread.start();
            }
        } catch (IOException e){
            assert false;
        }
    }
}