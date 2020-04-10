package Main;

import Entities.Executor;
import Entities.Job;
import Enumeration.BroadcastingType;
import Enumeration.LoggerPriority;
import Enumeration.SocketReceiverType;
import Messages.*;
import Network.*;
import Tasks.Pi;
import utils.ComputeEngine;
import utils.Logger;
import java.net.InetAddress;
import java.util.Scanner;

import static java.lang.System.exit;

public class ExecutorMain {
    public static final Integer clientsPort = 9669;
    public static final Integer executorsPort = 9670;
    public static final Integer multicastPort = 6789;
    public static final String relativePathToArchiveDir = "/ser/";
    public static Integer nThreads = Runtime.getRuntime().availableProcessors();;

    public static void shutdown(){
        Logger.log(LoggerPriority.NORMAL, "Shutdown");
        try {
            Message lmsg = new LeaveMessage();
            Broadcaster.getInstance().send(lmsg);
            //MulticastPublisher.send(lmsg);
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

        if (nThreads <= 0) {
            Logger.log(LoggerPriority.WARNING, "Error determining the number of cores. Using the default number: 2");
            nThreads = 2;
        }

        Logger.log(LoggerPriority.NOTIFICATION, "I'm up");
        Logger.log(LoggerPriority.NOTIFICATION,"Using " + nThreads + " cores.");

        Executor.getIstance();
        Broadcaster.getInstance(BroadcastingType.GLOBAL_TCP);

        SocketDatagramReceiver sdr = new SocketDatagramReceiver(executorsPort);
        sdr.start();

        SocketReceiver srToExecutors = new SocketReceiver(SocketReceiverType.TO_EXECUTOR);
        srToExecutors.start();

        SocketReceiver srToClient = new SocketReceiver(SocketReceiverType.TO_CLIENT);
        srToClient.start();

        Broadcaster.getInstance().sayHello();                           // tell others that i'm online

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run() {
                ExecutorMain.shutdown();
            }
        });

        ComputeEngine.getIstance().startRMI();

        Scanner scanner = new Scanner(System.in);
        Integer choice;
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