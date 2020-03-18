import Enumeration.LoggerPriority;
import Enumeration.MessageType;
import Messages.Message;
import Network.SocketBroadcaster;
import Network.SocketDatagramReceiver;
import Network.SocketReceiver;
import utils.Logger;

public class executorMain {
    public static Integer clientsPort = 9669;
    public static Integer executorsPort = 9670;

    public static void main(String[] args) throws Exception {
        Logger.log(LoggerPriority.NORMAL, "I'm up\nNotifyng others");

        Message msg = new Message(MessageType.JOIN_MESSAGE);
        SocketBroadcaster.send(executorsPort, msg);

        SocketDatagramReceiver sdr = new SocketDatagramReceiver(executorsPort);
        sdr.start();

        SocketReceiver srToExecutors = new SocketReceiver(executorsPort);
        srToExecutors.start();

        SocketReceiver srToClient = new SocketReceiver(clientsPort);
        srToClient.start();



        //SocketReceiver sm = new SocketReceiver();
        //sm.start();





        // inviare una richiesta in broacast
        // gestire risposte e costruire lo stato


        /*
        Lo stato di un executor cambia quando riceve un update da un altro executor.

        Quando un client fa una richiesta, l'executor interroga gli altri colleghi.
         */
    }

}
