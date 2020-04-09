package utils;

import Entities.Executor;
import Entities.Job;
import Enumeration.LoggerPriority;
import Main.ExecutorMain;
import Messages.FallenExecutor;
import Network.SocketBroadcaster;
import Tasks.Compute;
import Tasks.Task;
import Messages.ProposeJobMessage;
import Network.SocketSenderUnicast;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static Main.ExecutorMain.executorsPort;

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
    public <T> String executeTask(Task<T> t) throws RemoteException {
        Job job = new Job(t);
        ProposeJobMessage pjm = new ProposeJobMessage(job);
        InetAddress address = Executor.getIstance().proposeJob();
        InetAddress localAddress = NetworkUtilis.getLocalAddress();
        if (address.equals(localAddress)){
            Executor.getIstance().acceptJob(job);
        } else {
            while (true) {
                try {
                    SocketSenderUnicast.send(pjm, address, executorsPort);
                    break;
                } catch (ConnectException e) {
                    Logger.log(LoggerPriority.WARNING, "Chosen executor seems to be offline. I will try with another one");
                    SocketBroadcaster.send(executorsPort, new FallenExecutor(address));         // tell others
                    Executor.getIstance().removeExecutor(address);
                    address = Executor.getIstance().proposeJob();
                    continue;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return job.getID();
    }

    public void startRMI() {
        //System.setProperty("java.security.policy","file:///Users/alenichel/IdeaProjects/MartinaNichelini-DistributedJobScheduling/src/main/java/server.policy");
        //if (System.getSecurityManager() == null) {
        //    System.setSecurityManager(new SecurityManager());
        //}
        try {
            String name = "Compute";
            Compute engine = new ComputeEngine();
            Compute stub = (Compute) UnicastRemoteObject.exportObject(engine, 0);
            //Registry registry = LocateRegistry.getRegistry();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(name, stub);
            Logger.log(LoggerPriority.NOTIFICATION, "ComputeEngine bound");
        } catch (Exception e) {
            Logger.log(LoggerPriority.ERROR, "ComputeEngine exception:");
            e.printStackTrace();
        }
    }
}
