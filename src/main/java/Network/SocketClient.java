package Network;

import Enumeration.LoggerPriority;
import Messages.Message;
import utils.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class SocketClient {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean keepAlive;
    BlockingQueue queue = new SynchronousQueue();


    public SocketClient(String serverIP, int port) throws IOException, ClassNotFoundException {
        try {
            this.keepAlive = true;
            socket = new Socket(serverIP, port);
            Logger.log(LoggerPriority.NORMAL, "[*] Socket ready..\n");
            Logger.log(LoggerPriority.NORMAL, "[*] Connection established\n");
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());

            //oos.writeObject(new HandshakeConnectionMessage(nickname, password));

            Object rcv = ois.readObject();


        } catch (IOException e) {
            throw e;
        }

        Listener listener = new Listener();
        Sender sender = new Sender();
        sender.start();
        listener.start();
    }

    private class Listener extends Thread{

        @Override
        public void run(){
            Object in = null;
            while (!socket.isClosed() && keepAlive){
                try {
                    in = ois.readObject();
                } catch (EOFException e){
                    Logger.log(LoggerPriority.WARNING, "Connection was closed server side. Closing...");
                    System.exit(0);
                }
                catch (StreamCorruptedException e){
                    Logger.log(LoggerPriority.WARNING, ":SOCKETCLIENT_LISTENER: caught java.io.StreamCorruptedException");
                    break;
                }
                catch (ClassNotFoundException | IOException e){
                    Logger.log(LoggerPriority.ERROR, ":SOCKETCLIENT_LISTENER:" +  e.toString());
                    System.exit(1);
                }
            }
        }
    }

    private class Sender extends Thread{
        @Override
        public void run() {
            try {
                Message msg;
                while (keepAlive) {
                    msg = (Message) queue.take();
                    oos.writeObject(msg);
                    oos.flush();
                    oos.reset();
                }
            } catch (InterruptedException | IOException e) {
                Logger.log(LoggerPriority.ERROR, Arrays.toString(e.getStackTrace()));
            }
        }
    }

}