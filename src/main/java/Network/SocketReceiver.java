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

    private ServerSocket ssocket;
    private Integer executorToExecutorPort = 9670;

    @Override
    public void run() {
        try {
            ssocket = new ServerSocket(executorToExecutorPort);
            while (true) {
                Socket socket = this.ssocket.accept();
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