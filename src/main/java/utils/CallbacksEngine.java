package utils;

import Entities.Executor;
import Entities.Job;
import Enumeration.LoggerPriority;
import Messages.*;
import Network.SocketSenderUnicast;
import Main.ExecutorMain;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

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

                Executor.getIstance().getForeignCompletedJobs().put( ((UpdateTableMessage)message).getJobId() ,fromAddress);
                Executor.getIstance().printState();
                break;


            case RESULT_REQUEST_MESSAGE:
                ResultRequestMessage rrm = (ResultRequestMessage)message;
                String id = rrm.getJobId();
                Map<String, Job> jobs = Executor.getIstance().getIdToJob();

                // Socket
                if (jobs.containsKey(id)) {
                    Job js = jobs.get(id);
                    IKnowMessage rrm_toSend = new IKnowMessage(js.getStatus(), js.getResult());
                    oos.writeObject(rrm_toSend);
                    oos.close();
                }
                else if (Executor.getIstance().getForeignCompletedJobs().containsKey(id)){
                    ResultRequestMessage rrm_toSend = new ResultRequestMessage(id, true);
                    InetAddress ia = Executor.getIstance().getForeignCompletedJobs().get(id);
                    Message m = SocketSenderUnicast.sendAndWaitResponse(rrm_toSend, ia, ExecutorMain.executorsPort);
                    oos.writeObject(m);
                    oos.close();
                }
                else {
                    // From executor to executor request
                    if( rrm.getForwared() ){
                        IDontKnowMessage idkm = new IDontKnowMessage();
                        oos.writeObject(idkm);
                        oos.close();
                    } else {
                        ResultRequestMessage rrm_toSend = new ResultRequestMessage(id, true);

                        Set<InetAddress> addresses = Executor.getIstance().getExecutorToNumberOfJobs().keySet();

                        for (InetAddress r_ia : addresses){
                            Message m =  SocketSenderUnicast.sendAndWaitResponse(rrm_toSend, r_ia, ExecutorMain.executorsPort);
                            if (m instanceof IKnowMessage) {
                                IKnowMessage ikm = (IKnowMessage) m;
                                if (ikm.getJobStatus() == null){    //Executor has NOT the result, it will send you the owner address
                                    InetAddress ia = ikm.getActualOwner();
                                    ResultRequestMessage f_rrm = new ResultRequestMessage(id, true);
                                    Message fm = SocketSenderUnicast.sendAndWaitResponse(f_rrm, ia, ExecutorMain.executorsPort);
                                    oos.writeObject(fm);
                                    oos.close();
                                    return;
                                } else {        //Executor has the result
                                    oos.writeObject(m);
                                    oos.close();
                                    return;
                                }
                            } else if (m instanceof IDontKnowMessage){
                                continue;
                            }
                        }
                        IDontKnowMessage idkm = new IDontKnowMessage();
                        oos.writeObject(idkm);
                        oos.close();
                    }


                }
                break;

            default:
                Logger.log(LoggerPriority.WARNING, "Message type not recognized. It won't be handled");
                break;
        }
    }
}
