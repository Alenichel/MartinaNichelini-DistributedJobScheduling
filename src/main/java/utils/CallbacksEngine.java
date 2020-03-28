package utils;

import Entities.Executor;
import Entities.Job;
import Enumeration.JobStatus;
import Enumeration.LoggerPriority;
import Messages.*;
import Network.SocketSenderUnicast;
import Main.ExecutorMain;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

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

    public void handleCallback(Object msg, InetAddress fromAddress) throws InterruptedException, IOException, ClassNotFoundException {
        handleCallback(msg, fromAddress, null);
    }

    public void handleCallback(Object msg, InetAddress fromAddress, ObjectOutputStream oos) throws InterruptedException, IOException, ClassNotFoundException {
        Message message = ((Message)msg);
        Logger.log(LoggerPriority.NOTIFICATION, "Message of type " + message.getType() + " from " + fromAddress + " arrived.");
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
                Integer nJobs = ((UpdateTableMessage)message).getnJobs();
                Executor.getIstance().updateTable(fromAddress, nJobs);
                break;

            case RESULT_REQUEST_MESSAGE:
                String id = ((ResultRequestMessage)message).getJobId();
                Map<String, Job> jobs = Executor.getIstance().getIdToJob();
                if (jobs.containsKey(id)) {
                    Job js = jobs.get(id);
                    ResultResponseMessage rrm = new ResultResponseMessage(js.getStatus(), js.getResult());
                    oos.writeObject(rrm);
                    oos.close();
                }
                else {}//TODO inoltra agli altri}
                break;

            default:
                Logger.log(LoggerPriority.WARNING, "Message type not recognized. It won't be handled");
                break;
        }
    }
}
