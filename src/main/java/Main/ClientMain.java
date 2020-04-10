package Main;

import java.net.ConnectException;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import Enumeration.JobStatus;
import Enumeration.LoggerPriority;
import Messages.*;
import Network.SocketSenderUnicast;
import Tasks.Compute;
import Tasks.Pi;
import Tasks.SleepTask;
import utils.Logger;

public class ClientMain {
    public static void main(String args[]) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        String name = "Compute";

        Scanner scanner = new Scanner(System.in);
        Logger.log(LoggerPriority.NORMAL, "I'm up and running");
        Logger.log(LoggerPriority.NORMAL, "Please write down the address of the executor you want to connect. Blank for localhost");
        String host = scanner.nextLine();
        host = host.equals("") ? "127.0.0.1" : host;

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                Logger.log(LoggerPriority.NORMAL, "Shutdown");
            }
        });

        Registry registry = null;

        try {
            registry = LocateRegistry.getRegistry(host);
            Compute comp = (Compute) registry.lookup(name);
            Logger.log(LoggerPriority.NOTIFICATION, "RMI ready");

            Integer choice;
            while (true){
                System.out.println( "***************************" +
                        "\n1) Send pi task" +
                        "\n2) Send sleep task" +
                        "\n3) Request return value" +
                        "\n9) Exit" +
                        "\n***************************");
                String tokens[] = scanner.nextLine().split("");

                try { choice = Integer.parseInt(tokens[0]); } catch (NumberFormatException e){
                    Logger.log(LoggerPriority.NOTIFICATION, "Not valid number");
                    continue; }

                switch (choice){
                    case 1:
                        Pi piTask = new Pi(Integer.parseInt("80000"));
                        String piId = null;
                        piId = comp.executeTask(piTask);
                        System.out.println("The job with id: " + piId + " was accepted");
                        break;
                    case 2:
                        SleepTask sleepTask = new SleepTask();
                        String sleepId = null;
                        sleepId = comp.executeTask(sleepTask);
                        System.out.println("The job with id: " + sleepId + " was accepted");
                        break;
                    case 3:
                        Logger.log(LoggerPriority.NORMAL,"Insert job id:");
                        String jobId = scanner.nextLine();
                        ResultRequestMessage rrm = new ResultRequestMessage(jobId, false);
                        try {
                            IKnowMessage rsp = (IKnowMessage) SocketSenderUnicast.sendAndWaitResponse(rrm, InetAddress.getByName(host), ExecutorMain.clientsPort);
                            Logger.log(LoggerPriority.NOTIFICATION, "Received response");

                            Logger.log(LoggerPriority.NOTIFICATION, "Task status is: " + rsp.getJobStatus());
                            Logger.log(LoggerPriority.NOTIFICATION, "Response lenght is: " + (rsp.getResult().toString().length() -2));
                            if (rsp.getJobStatus() == JobStatus.COMPLETED){
                                Logger.log(LoggerPriority.NOTIFICATION, "Returned value is: " + rsp.getResult().toString());
                            }

                        }catch (ClassCastException e){
                            Logger.log(LoggerPriority.NOTIFICATION, "Request job doesn't not exist (or has been lost)" );
                        }
                        break;
                    case 4:
                        Integer i = 0;
                        while (i < 50){
                            Pi taskk = new Pi(i + 80000);
                            String idd = null;
                            idd = comp.executeTask(taskk);
                            System.out.println("The job with id: " + idd + " was accepted");
                            i++;
                            Thread.sleep(100);
                        }
                        break;
                    case 9:
                        return;
                    default:
                        break;
                }
            }
        } catch (ConnectException | java.rmi.ConnectException e){
            Logger.log(LoggerPriority.WARNING, "Server went offline. Quitting..");
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
