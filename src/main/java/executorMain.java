import Enumeration.LoggerPriority;
import Network.SocketReceiver;
import utils.Logger;

public class executorMain {

    public static void main(String[] args) {
        Logger.log(LoggerPriority.NORMAL, "Hello World");
        SocketReceiver sm = new SocketReceiver();
        sm.start();





        // inviare una richiesta in broacast
        // gestire risposte e costruire lo stato


        /*
        Lo stato di un executor cambia quando riceve un update da un altro executor.

        Quando un client fa una richiesta, l'executor interroga gli altri colleghi.
         */
    }

}
