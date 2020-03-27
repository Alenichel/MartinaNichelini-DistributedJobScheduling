package utils;

import Entities.Executor;
import Entities.Job;
import Enumeration.LoggerPriority;
import Interfaces.Compute;
import Interfaces.Task;
import Messages.ProposeJobMessage;
import Network.SocketSenderUnicast;
import Tasks.Pi;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static main.executorMain.executorsPort;

public class ComputeEngine implements Compute {

    public static ComputeEngine instance = null;

    private ComputeEngine() {
        super();
    }

    public static ComputeEngine getIstance() {
        if(instance==null)
            synchronized(Executor.class) {
                if( instance == null )
                    instance = new ComputeEngine();
            }
        return instance;
    }

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        Job j = new Job(t);
        ProposeJobMessage pjm = new ProposeJobMessage(j);
        InetAddress a = Executor.getIstance().proposeJob();
        try {
            SocketSenderUnicast.send(pjm, a, executorsPort);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return t.execute();
    }

    public void startRMI() {
        //System.setProperty("java.security.policy","file:///Users/alenichel/IdeaProjects/MartinaNichelini-DistributedJobScheduling/src/main/java/server.policy");
        //if (System.getSecurityManager() == null) {
        //    System.setSecurityManager(new SecurityManager());
        //}
        try {
            String name = "Compute";
            Compute engine = new ComputeEngine();
            Compute stub =
                    (Compute) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            Logger.log(LoggerPriority.NOTIFICATION, "ComputeEngine bound");
        } catch (Exception e) {
            Logger.log(LoggerPriority.ERROR, "ComputeEngine exception:");
            e.printStackTrace();
        }
    }
}
