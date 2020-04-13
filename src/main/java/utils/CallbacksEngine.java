package utils;

import Entities.Executor;
import Entities.Job;
import Enumeration.LoggerPriority;
import Messages.*;
import Network.SocketSenderUnicast;
import Main.ExecutorMain;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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


    private void sendDKM(ObjectOutputStream oos) throws IOException {
        IDontKnowMessage idkm = new IDontKnowMessage();
        oos.writeObject(idkm);
        oos.close();
    }

    public void handleCallback(Object msg, InetAddress fromAddress, ObjectOutputStream oos) throws InterruptedException, IOException, ClassNotFoundException {
        Message message = ((Message)msg);
        Logger.log(LoggerPriority.NOTIFICATION, "Message of type " + message.getType() + " from " + fromAddress + " arrived.");
        switch (message.getType()){
            case PONG_MESSAGE:
                Integer n = ((PongMessage)msg).getNumberOfJobs();
                Integer nThread = ((PongMessage)msg).getNThread();
                Executor.getIstance().addExecutor(fromAddress, n, nThread);
                break;

            case PROPOSE_JOB:
                Job j = ((ProposeJobMessage)msg).getJob();
                Executor.getIstance().acceptJob(j);
                break;

            case JOIN_MESSAGE:
                try {
                    Boolean toAdjust;
                    if (fromAddress.isAnyLocalAddress()){
                        toAdjust = false;
                    } else {
                        toAdjust = true;
                    }
                    Message pongMessage = new PongMessage(Executor.getIstance().getNumberOfJobs(), Executor.getIstance().getExecutorToNumberOfJobs().keySet().stream().collect(Collectors.toList()), toAdjust, ExecutorMain.nThreads);
                    if (  ((JoinMessage)msg).getJustExploring()  ) {
                        oos.writeObject(pongMessage);
                        Logger.log(LoggerPriority.NOTIFICATION, "Directly responded to exploring message.");
                    } else {
                        Executor.getIstance().addExecutor(fromAddress, 0, ((JoinMessage)message).getNThreads());
                        SocketSenderUnicast.send(pongMessage, fromAddress, ExecutorMain.executorsPort);
                    }
                } catch (IOException | ClassNotFoundException e){
                    Logger.log(LoggerPriority.ERROR, "Error while sending back pong");
                    e.printStackTrace();
                    Executor.getIstance().removeExecutor(fromAddress);
                    Logger.log(LoggerPriority.WARNING, "Faulty executor removed");
                }
                break;

            case LEAVE_MESSAGE:
                Executor.getIstance().removeExecutor(fromAddress);
                break;

            case FALLEN_EXECUTOR:
                Executor.getIstance().removeExecutor(((FallenExecutor)msg).getLostExecutorAddress());
                break;

            case UPDATE_TABLE_MESSAGE:
                Integer nJobs = ((UpdateTableMessage)message).getnJobs();
                Executor.getIstance().updateTable(fromAddress, nJobs);

                Executor.getIstance().getForeignCompletedJobs().put( ((UpdateTableMessage)message).getJobId() ,fromAddress);
                Executor.getIstance().printState();
                if (nJobs == 0){
                    Executor.getIstance().reassignJob(fromAddress);
                }
                break;


            case RESULT_REQUEST_MESSAGE:
                ResultRequestMessage rrm = (ResultRequestMessage)message;
                String id = rrm.getJobId();
                Map<String, Job> jobs = Executor.getIstance().getIdToJob();
                // Directly respond if you have the result
                if (jobs.containsKey(id)) {
                    Logger.log(LoggerPriority.DEBUG, "RESULT_REQUEST_MESSAGE_HANDLER: Contacted executor is the owner of the job, now answering");
                    Job js = jobs.get(id);
                    IKnowMessage rrm_toSend = new IKnowMessage(js.getStatus(), js.getResult());
                    oos.writeObject(rrm_toSend);
                    oos.close();
                }
                // Directly contact the owner of the job if know it
                else if (Executor.getIstance().getForeignCompletedJobs().containsKey(id)){
                    try {
                        Logger.log(LoggerPriority.DEBUG, "RESULT_REQUEST_MESSAGE_HANDLER: Contacted executor knew the job's owner, it's now directly contacting it.");
                        ResultRequestMessage rrm_toSend = new ResultRequestMessage(id, true);
                        InetAddress ia = Executor.getIstance().getForeignCompletedJobs().get(id);
                        Message m = SocketSenderUnicast.sendAndWaitResponse(rrm_toSend, ia, ExecutorMain.executorsPort);
                        oos.writeObject(m);
                        oos.close();
                    } catch (ConnectException e) {
                        Logger.log(LoggerPriority.WARNING, "RESULT_REQUEST_MESSAGE_HANDLER: Contacted owner did not respond. This exception has been handled");
                        sendDKM(oos);
                    }
                }
                else {
                    //If you know nothing (like JS) just say it (and this message has been already forwarded)
                    if( rrm.getForwared() ){
                        Logger.log(LoggerPriority.WARNING, "RESULT_REQUEST_MESSAGE_HANDLER: This executor has been asked for a job result, but it didn't know anything");
                        sendDKM(oos);
                    } else {
                        // if you still have to handle the request (the client asked to you)
                        ResultRequestMessage rrm_toSend = new ResultRequestMessage(id, true);

                        Set<InetAddress> addresses = Executor.getIstance().getExecutorToNumberOfJobs().keySet();
                        addresses.remove(NetworkUtilis.getLocalAddress());
                        for (InetAddress r_ia : addresses){
                            Message m =  SocketSenderUnicast.sendAndWaitResponse(rrm_toSend, r_ia, ExecutorMain.executorsPort);
                            if (m instanceof IKnowMessage) {
                                IKnowMessage ikm = (IKnowMessage) m;
                                //Contacted executor knows the address of the owner, you ask him
                                if (ikm.getJobStatus() == null){
                                    try {
                                        InetAddress ia = ikm.getActualOwner();
                                        ResultRequestMessage f_rrm = new ResultRequestMessage(id, true);
                                        Message fm = SocketSenderUnicast.sendAndWaitResponse(f_rrm, ia, ExecutorMain.executorsPort);
                                        oos.writeObject(fm);
                                        oos.close();
                                        Logger.log(LoggerPriority.WARNING, "RESULT_REQUEST_MESSAGE_HANDLER: contacted owner did answer with the result. Forwarded to client");
                                        return;
                                    } catch (ConnectException e){
                                        Logger.log(LoggerPriority.WARNING, "RESULT_REQUEST_MESSAGE_HANDLER: Contacted owner did not respond. This exception has been handled");
                                        sendDKM(oos);
                                        return;
                                    }
                                } else {        //Contacted executor directly has the result
                                    oos.writeObject(m);
                                    oos.close();
                                    return;
                                }
                            } else if (m instanceof IDontKnowMessage){  //if conctacted executor does know nothing, just skip to the next in line
                                continue;
                            }
                        }
                        //if no executor knows the result, surrender
                        sendDKM(oos);
                    }


                }
                break;

            default:
                Logger.log(LoggerPriority.WARNING, "Message type not recognized. It won't be handled");
                break;
        }
    }

}
