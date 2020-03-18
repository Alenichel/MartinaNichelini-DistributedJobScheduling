package Network;

import Enumeration.LoggerPriority;
import utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketReceiver extends Thread {

    private Integer port;

    public SocketReceiver(Integer port){
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket ssocket = new ServerSocket(port);
            while (true) {
                Socket socket = ssocket.accept();
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line = reader.readLine();
                Logger.log(LoggerPriority.NORMAL, line);
            }
        } catch (IOException e) {
            Logger.log(LoggerPriority.ERROR, e.toString());
        }
    }


}