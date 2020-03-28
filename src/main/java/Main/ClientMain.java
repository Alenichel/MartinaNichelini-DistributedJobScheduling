package Main;

import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.math.BigDecimal;
import java.util.Scanner;

import Entities.Executor;
import Entities.Job;
import Enumeration.LoggerPriority;
import Messages.ProposeJobMessage;
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
        Logger.log(LoggerPriority.NORMAL, "Please write down the address of the executor you want to connect. Blank for localhost");
        String host = scanner.nextLine();
        host = host.equals("") ? "localhost" : host;

        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(host);
            Compute comp = (Compute) registry.lookup(name);
            Logger.log(LoggerPriority.NOTIFICATION, "RMI ready");

            Integer choice;
            while (true){
                System.out.println( "***************************" +
                        "\n1) Send pi task" +
                        "\n9) Exit" +
                        "\n***************************");
                String tokens[] = scanner.nextLine().split("");

                try { choice = Integer.parseInt(tokens[0]); } catch (NumberFormatException e){
                    Logger.log(LoggerPriority.NOTIFICATION, "Not valid number");
                    continue; }

                switch (choice){
                    case 1:
                        Pi task = new Pi(Integer.parseInt("100000"));
                        String id = null;
                        id = comp.executeTask(task);
                        System.out.println("The job with id: " + id + " was accepted");
                        break;
                    case 9:
                        return;
                    default:
                        break;
                }
            }
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

    }
}
