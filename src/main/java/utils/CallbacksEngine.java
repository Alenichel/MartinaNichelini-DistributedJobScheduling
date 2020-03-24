package utils;

import Entities.Executor;
import Entities.Job;
import Enumeration.JobType;
import Enumeration.LoggerPriority;
import Enumeration.MessageType;
import Messages.Message;
import Messages.PongMessage;
import Network.SocketSenderUnicast;
import main.executorMain;

import java.io.IOException;
import java.net.InetAddress;

import static Enumeration.MessageType.PONG_MESSAGE;
import static Enumeration.MessageType.PROPOSE_JOB;

public class CallbacksEngine {

    public static CallbacksEngine instance = null;

    private Executor executor;

    public static CallbacksEngine getIstance() {
        if(instance==null)
            synchronized(CallbacksEngine.class) {
                if( instance == null )
                    instance = new CallbacksEngine();
            }
        return instance;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void handleCallback(Object msg, InetAddress fromAddress) throws InterruptedException {
        Message message = ((Message)msg);
        switch (message.getType()){
            case PONG_MESSAGE:
                Integer n = ((PongMessage)msg).getNumberOfJobs();
                this.executor.addExecutor(fromAddress, n);
                break;

            case PROPOSE_JOB:
                Job j = new Job(JobType.VERY_COMPLEX_JOB);
                this.executor.acceptJob(j);

            default:
                Logger.log(LoggerPriority.WARNING, "Message type not recognized. It won't be handled");
        }
    }

    public void handleCallback(String msg, InetAddress fromAddress){
        switch (msg){
            case "JOIN_MESSAGE":
                try {
                    this.executor.addExecutor(fromAddress, 0);
                    Message pongMessage = new PongMessage(this.executor.getNumberOfJobs());
                    SocketSenderUnicast.send(pongMessage, fromAddress, executorMain.executorsPort);
                } catch (IOException | ClassNotFoundException e){
                    Logger.log(LoggerPriority.ERROR, "Error while sending back pong");
                    System.out.println(e.toString());
                    e.printStackTrace();
                }
                break;

            case "LEAVE_MESSAGE":
                this.executor.removeExecutor(fromAddress);
                break;

            default:
                break;
        }
    }
}
