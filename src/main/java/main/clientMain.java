package main;

import Network.SocketSenderUnicast;

import java.io.IOException;

public class clientMain {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        SocketSenderUnicast ssu = new SocketSenderUnicast(executorMain.executorsPort, "localhost");
        ssu.send("prova");
    }
}
