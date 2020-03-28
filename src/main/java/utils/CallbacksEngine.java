package utils;

import Entities.Executor;
import Entities.Job;
import Enumeration.LoggerPriority;
import Messages.Message;
import Messages.PongMessage;
import Messages.ProposeJobMessage;
import Messages.UpdateTableMessage;
import Network.SocketSenderUnicast;
import Main.ExecutorMain;

import java.io.IOException;
import java.net.InetAddress;

public class CallbacksEngine {

    public static CallbacksEngine instance = null;

    public static CallbacksEngine getIstance() {
        if(instance==null)
            synchronized(CallbacksEngine.class) {
                if( instance == null )
                    instance = new CallbacksEngine();
            }
        return instance;
    }

    public void handleCallback(Object msg, InetAddress fromAddress) throws InterruptedException {
        Message message = ((Message)msg);
        switch (message.getType()){
            case PONG_MESSAGE:
                Integer n = ((PongMessage)msg).getNumberOfJobs();
                Executor.getIstance().addExecutor(fromAddress, n);
                break;

            case PROPOSE_JOB:
                Job j = ((ProposeJobMessage)msg).getJob();
                Executor.getIstance().acceptJob(j);
                break;

            case JOIN_MESSAGE:
                try {
                    Executor.getIstance().addExecutor(fromAddress, 0);
                    Message pongMessage = new PongMessage(Executor.getIstance().getNumberOfJobs());
                    SocketSenderUnicast.send(pongMessage, fromAddress, ExecutorMain.executorsPort);
                } catch (IOException | ClassNotFoundException e){
                    Logger.log(LoggerPriority.ERROR, "Error while sending back pong");
                    System.out.println(e.toString());
                    e.printStackTrace();
                }
                break;

            case LEAVE_MESSAGE:
                Executor.getIstance().removeExecutor(fromAddress);
                break;

            case UPDATE_TABLE_MESSAGE:
                Logger.log(LoggerPriority.NOTIFICATION, "Update table message arrived");
                Integer nJobs = ((UpdateTableMessage)message).getnJobs();
                Executor.getIstance().updateTable(fromAddress, nJobs);
                break;

            default:
                Logger.log(LoggerPriority.WARNING, "Message type not recognized. It won't be handled");
                break;
        }
    }
}
