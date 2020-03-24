package main;

import Entities.Executor;
import Entities.Job;
import Enumeration.JobType;
import Enumeration.LoggerPriority;
import Enumeration.MessageType;
import Enumeration.SocketReceiverType;
import Messages.*;
import Network.SocketBroadcaster;
import Network.SocketDatagramReceiver;
import Network.SocketReceiver;
import Network.SocketSenderUnicast;
import utils.CallbacksEngine;
import utils.Logger;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Scanner;

public class executorMain  {
    public static Integer clientsPort = 9669;
    public static Integer executorsPort = 9670;

    public static void main(String[] args) throws Exception {
        Logger.log(LoggerPriority.NOTIFICATION, "I'm up");

        Executor myself = new Executor();

        SocketDatagramReceiver sdr = new SocketDatagramReceiver(executorsPort, myself);
        sdr.start();

        SocketReceiver srToExecutors = new SocketReceiver(SocketReceiverType.TO_EXECUTOR);
        srToExecutors.start();

        SocketReceiver srToClient = new SocketReceiver(SocketReceiverType.TO_CLIENT);
        srToClient.start();

        CallbacksEngine.getIstance().setExecutor(myself);

        Message msg = new JoinMessage();
        SocketBroadcaster.send(executorsPort, msg);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                Logger.log(LoggerPriority.NORMAL, "Shutdown");
                try {
                    Message lmsg = new LeaveMessage();
                    SocketBroadcaster.send(executorsPort, lmsg);
                } catch (Exception e) {}
            }
        });

        Scanner scanner = new Scanner(System.in);
        Integer choice;
        while (true){
            System.out.println( "***************************" +
                    "\n   Number of active jobs: " + myself.getNumberOfJobs().toString() +
                    "\n   Select: " +
                    "\n1) New job" +
                    "\n9) Exit" +
                    "\n***************************");
            String tokens[] = scanner.nextLine().split("");
            try {
                choice = Integer.parseInt(tokens[0]);
            } catch (NumberFormatException e){
                Logger.log(LoggerPriority.NOTIFICATION, "Non valid number");
                continue;
            }
            switch (choice){
                case 1:
                    //Job j = new Job(JobType.VERY_COMPLEX_JOB);
                    //ProposeJobMessage pjm = new ProposeJobMessage(j);
                    InetAddress a = myself.proposeJob();
                    SocketSenderUnicast.send(new DumbMessage(), a, executorsPort);
                    break;
                case 9:
                    return;
                default:
                    break;
            }
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
