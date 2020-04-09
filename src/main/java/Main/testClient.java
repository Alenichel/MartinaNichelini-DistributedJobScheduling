package Main;

import Messages.JoinMessage;
import Messages.Message;
import Network.MulticastPublisher;

import java.io.IOException;

public class testClient {

    public static void main(String[] args) throws IOException {
        Message msg = new JoinMessage();
        MulticastPublisher.send(msg);
    }
}
