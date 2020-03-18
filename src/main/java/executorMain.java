import Enumeration.LoggerPriority;
import Network.SocketBroadcaster;
import Network.SocketDatagramReceiver;
import Network.SocketReceiver;
import utils.Logger;

import java.net.UnknownHostException;

public class executorMain {

    public static Integer port = 9670;

    public static void main(String[] args) throws Exception {
        Logger.log(LoggerPriority.NORMAL, "I'm up\nNotifyng others");
        SocketBroadcaster.send(port, "Sono su");
        SocketDatagramReceiver sdr = new SocketDatagramReceiver(port);
        sdr.start();




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
