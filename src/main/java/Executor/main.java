package Executor;

import Network.SocketServer;

public class main {

    public static void main(String[] args) {
        System.out.println("Hello World");
        SocketServer ss = new SocketServer();
        ss.start();
    }

}
