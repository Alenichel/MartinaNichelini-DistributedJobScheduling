package Main;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import Enumeration.JobStatus;
import Enumeration.LoggerPriority;
import Enumeration.TaskType;
import Messages.ResultRequestMessage;
import Messages.ResultResponseMessage;
import Network.SocketSenderUnicast;
import Tasks.Compute;
import Tasks.Pi;
import utils.Logger;

public class ClientMain {
    public static void main(String args[]) {
        /*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/
        String name = "Compute";

        Scanner scanner = new Scanner(System.in);
        Logger.log(LoggerPriority.NORMAL, "I'm up and running");
        /*Logger.log(LoggerPriority.NORMAL, "Please write down the address of the executor you want to connect. Blank for localhost");
        String host = scanner.nextLine();
        host = host.equals("") ? "127.0.0.1" : host;*(

         */

        String host = "127.0.0.1";

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
                        Pi task = new Pi(Integer.parseInt("500000"));
                        String id = null;
                        id = comp.executeTask(task);
                        System.out.println("The job with id: " + id + " was accepted");
                        break;
                    case 3:
                        Logger.log(LoggerPriority.NORMAL,"Insert job id:");
                        String jobId = scanner.nextLine();
                        ResultRequestMessage rrm = new ResultRequestMessage(jobId);
                        ResultResponseMessage rsp = (ResultResponseMessage) SocketSenderUnicast.sendAndWaitResponse(rrm, InetAddress.getByName(host), ExecutorMain.clientsPort);
                        Logger.log(LoggerPriority.NOTIFICATION, "Received response");

                        Logger.log(LoggerPriority.NOTIFICATION, "Task status is: " + rsp.getJobStatus());
                        //BigDecimal bd = (BigDecimal) rsp.getResult();
                        if (rsp.getJobStatus() == JobStatus.COMPLETED){
                            Logger.log(LoggerPriority.NOTIFICATION, rsp.getResult().toString());
                        }


                        break;
                    case 9:
                        return;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
