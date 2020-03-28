package Main;

import Entities.Executor;
import Entities.Job;
import Enumeration.LoggerPriority;
import Enumeration.SocketReceiverType;
import Messages.*;
import Network.SocketBroadcaster;
import Network.SocketDatagramReceiver;
import Network.SocketReceiver;
import Network.SocketSenderUnicast;
import Tasks.Pi;
import utils.ComputeEngine;
import utils.Logger;

import java.net.InetAddress;
import java.util.Scanner;

public class ExecutorMain {
    public static Integer clientsPort = 9669;
    public static Integer executorsPort = 9670;

    public static void main(String[] args) throws Exception {
        Logger.log(LoggerPriority.NOTIFICATION, "I'm up");

        SocketDatagramReceiver sdr = new SocketDatagramReceiver(executorsPort);
        sdr.start();

        SocketReceiver srToExecutors = new SocketReceiver(SocketReceiverType.TO_EXECUTOR);
        srToExecutors.start();

        SocketReceiver srToClient = new SocketReceiver(SocketReceiverType.TO_CLIENT);
        srToClient.start();

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

        ComputeEngine.getIstance().startRMI();

        Scanner scanner = new Scanner(System.in);
        Integer choice;
        while (true){
            System.out.println( "***************************" +
                    "\n   Number of active jobs: " + Executor.getIstance().getNumberOfJobs().toString() +
                    "\n   Select: " +
                    "\n1) New job" +
                    "\n9) Exit" +
                    "\n***************************");
            String tokens[] = scanner.nextLine().split("");
            try {
                choice = Integer.parseInt(tokens[0]);
            } catch (NumberFormatException e){
                Logger.log(LoggerPriority.NOTIFICATION, "Not valid number");
                continue;
            }
            switch (choice){
                case 1:
                    Pi pi = new Pi(10);
                    Job j = new Job(pi);
                    ProposeJobMessage pjm = new ProposeJobMessage(j);
                    InetAddress a = Executor.getIstance().proposeJob();
                    SocketSenderUnicast.send(pjm, a, executorsPort);
                    break;
                case 9:
                    return;
                default:
                    break;
            }
        }
    }

}
