package main;

import Entities.Executor;
import Enumeration.LoggerPriority;
import Enumeration.MessageType;
import Enumeration.SocketReceiverType;
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

        Executor myself = new Executor();

        Message msg = new Message(MessageType.JOIN_MESSAGE);
        SocketBroadcaster.send(executorsPort, msg);

        try {
            SocketDatagramReceiver sdr = new SocketDatagramReceiver(executorsPort, myself);
            sdr.start();

            SocketReceiver srToExecutors = new SocketReceiver(SocketReceiverType.TO_EXECUTOR);
            srToExecutors.start();

            SocketReceiver srToClient = new SocketReceiver(SocketReceiverType.TO_CLIENT);
            srToClient.start();

        } catch (Exception e) {
            Message lmsg = new Message(MessageType.LEAVE_MESSAGE);
            SocketBroadcaster.send(executorsPort, lmsg);
        }

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
