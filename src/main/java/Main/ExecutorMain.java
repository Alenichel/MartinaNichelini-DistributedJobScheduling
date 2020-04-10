package Main;

import Entities.Executor;
import Enumeration.BroadcastingType;
import Enumeration.LoggerPriority;
import Enumeration.SocketReceiverType;
import Messages.*;
import Network.*;
import utils.ComputeEngine;
import utils.Logger;
import utils.NetworkUtilis;

import java.net.InetAddress;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.lang.System.exit;

public class ExecutorMain {
    public static final Integer clientsPort = 9669;
    public static final Integer executorsPort = 9670;
    public static final Integer multicastPort = 6789;
    public static final Integer RMIPort = 1099;
    public static final String relativePathToArchiveDir = "/ser/";
    public static Integer nThreads = Runtime.getRuntime().availableProcessors();
    public static final InetAddress localIP = NetworkUtilis.getLocalAddress();
    public static final InetAddress externalIP = NetworkUtilis.getExternalAddress();
    public static BroadcastingType bt;

    public static void shutdown(){
        Logger.log(LoggerPriority.NORMAL, "Shutdown");
        try {
            Message lmsg = new LeaveMessage();
            Broadcaster.getInstance().send(lmsg);
            Executor.getIstance().saveKnownExecutors();
        } catch (Exception e) {}
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");

        String WD = System.getProperty("user.dir");
        String repoRelativePath = "src/main/java";

        if (!WD.substring(WD.length() - repoRelativePath.length()).equals(repoRelativePath)){
            Logger.log(LoggerPriority.ERROR, "(fatal) Working Directory must be <relative_path_to_the_repo>/src/main/java");
            return;
        }

        Scanner s = new Scanner(System.in);
        System.out.println("\nChoose: \n1) for using LOCAL_MODE only\n2) for using GLOBAL_MODE.\nDefault: 1\n");
        Integer choice = 1;
        try {
            choice = Integer.parseInt(s.nextLine());
        } catch (InputMismatchException | NumberFormatException e){
            choice = 1;
        }

        switch(choice){
            case 1:
                bt = BroadcastingType.LOCAL_UDP;
                break;
            case 2:
                bt = BroadcastingType.GLOBAL_TCP;
                break;
            default:
                bt = BroadcastingType.LOCAL_UDP;
        }

        Logger.log(LoggerPriority.NOTIFICATION, "Choosen type: " + bt);

        Logger.log(LoggerPriority.NORMAL, "\n\n\n\n\n\n\n\n\n");
        Logger.log(LoggerPriority.NORMAL, "++++++++++++++++++++++++++++++");
        Logger.log(LoggerPriority.NOTIFICATION, "I'm up");
        if (nThreads <= 0) {
            Logger.log(LoggerPriority.WARNING, "Error determining the number of cores. Using the default number: 2");
            nThreads = 2;
        } else {
            Logger.log(LoggerPriority.NOTIFICATION, "Using " + nThreads + " cores.");
        }

        Executor.getIstance();
        Broadcaster.getInstance(bt);

        SocketDatagramReceiver sdr = new SocketDatagramReceiver(executorsPort);
        if (bt == BroadcastingType.LOCAL_UDP) {
            sdr.start();
            Logger.log(LoggerPriority.NOTIFICATION, "Datagram receiver started on port: " + executorsPort);
        }

        SocketReceiver srToExecutors = new SocketReceiver(SocketReceiverType.TO_EXECUTOR);
        srToExecutors.start();
        Logger.log(LoggerPriority.NOTIFICATION, "Socket TCP executor receiver started on port: " + executorsPort);

        if (bt == BroadcastingType.GLOBAL_TCP){
            //NetworkUtilis.checkPortOpeness(executorsPort);
            Logger.log(LoggerPriority.WARNING, "Please mind that port " + executorsPort + " must be open.");
        }

        SocketReceiver srToClient = new SocketReceiver(SocketReceiverType.TO_CLIENT);
        srToClient.start();
        Logger.log(LoggerPriority.NOTIFICATION, "Socket TCP client receiver started on port: " + clientsPort);

        ComputeEngine.getIstance().startRMI();
        Logger.log(LoggerPriority.NOTIFICATION, "RMI listener started on port: " + RMIPort);

        Logger.log(LoggerPriority.NOTIFICATION, "My local ip address is: " + localIP);
        Logger.log(LoggerPriority.NOTIFICATION, "My external ip address is: " + externalIP);

        Logger.log(LoggerPriority.NORMAL, "++++++++++++++++++++++++++++++");

        Broadcaster.getInstance().sayHello();                           // tell others that i'm online

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run() {
                ExecutorMain.shutdown();
            }
        });

        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println( "***************************" +
                    "\n   Number of active jobs: " + Executor.getIstance().getNumberOfJobs().toString() +
                    "\n   Select: " +
                    //"\n1) New job" +
                    "\n9) Exit" +
                    "\n***************************");
            String tokens[] = scanner.nextLine().split("");
            try {
                choice = Integer.parseInt(tokens[0]);
            } catch (NumberFormatException e){
                continue;
            }
            switch (choice){
                /*case 1:
                    Pi pi = new Pi(10);
                    Job j = new Job(pi);
                    ProposeJobMessage pjm = new ProposeJobMessage(j);
                    InetAddress a = Executor.getIstance().proposeJob();
                    SocketSenderUnicast.send(pjm, a, executorsPort);
                    break;*/
                case 9:
                    exit(0);
                default:
                    break;
            }
        }
    }

}