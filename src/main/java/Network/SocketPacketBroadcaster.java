package Network;

import Entities.Executor;
import Enumeration.LoggerPriority;
import Main.ExecutorMain;
import Messages.JoinMessage;
import Messages.Message;
import Messages.PongMessage;
import utils.Logger;
import utils.NetworkUtilis;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import static java.lang.System.exit;

public class SocketPacketBroadcaster implements BroadcastingUnit{

    private Integer port;

    public SocketPacketBroadcaster(Integer port) {
        this.port = port;
        this.loadKnownExecutors();

    }

    private PongMessage manuallyAskForHost(){
        Scanner scanner = new Scanner(System.in);
        JoinMessage jm = new JoinMessage();
        jm.setJustExploring(true);
        PongMessage pm;
        while (true) {
            String line = scanner.nextLine();
            try {
                if (line.equals("skip")){
                    return null;
                }
                if (line.equals("q")){
                    exit(1);
                }
                InetAddress a = InetAddress.getByName(line);
                pm = (PongMessage) SocketSenderUnicast.sendAndWaitResponse(jm, a, this.port);
                break;

            } catch (ConnectException | NoRouteToHostException e){
                Logger.log(LoggerPriority.ERROR, "Connection error. Try again with another host");
                continue;
            }
            catch (UnknownHostException e) {
                Logger.log(LoggerPriority.ERROR, "Not valid host inserted");
                continue;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Logger.log(LoggerPriority.ERROR, "Error while contacting the host, try again or type: 'q' to quit.");
            }
        }
        return pm;
    }

    public void sayHello(){
        ArrayList<InetAddress> kh = Executor.getIstance().getKnownExecutors();
        PongMessage pm = null;
        JoinMessage jm = new JoinMessage();
        jm.setJustExploring(true);

        if (kh.isEmpty()) {                                                            // if there is not any known host
            Logger.log(LoggerPriority.WARNING, "No known executor found, please manually insert a known host or type 'skip' if you are the first one: ");
            pm = manuallyAskForHost();

            if (pm == null){
                Logger.log(LoggerPriority.NOTIFICATION, "You are the first and the only executor");
                return;
            }

            for (InetAddress ia : pm.getKnownHosts() ){
                if (!kh.contains(ia)){
                    kh.add(ia);
                }
            }
        }

        else {
            Logger.log(LoggerPriority.NOTIFICATION, "Trying contacting host in the list..");
            for (InetAddress ia : kh){
                try {
                    pm = (PongMessage) SocketSenderUnicast.sendAndWaitResponse(jm, ia, this.port);
                    Logger.log(LoggerPriority.NOTIFICATION, "Host: " + ia.toString() + " responded.");
                    break;
                } catch (IOException | ClassNotFoundException e){
                    Logger.log(LoggerPriority.DEBUG, "Host " + ia.toString() + " is offline. Trying with the next one");
                    pm = null;
                    continue;
                }
            }

            if (pm == null){
                Logger.log(LoggerPriority.WARNING, "No executor in the list responded, please manually insert one");
                pm = manuallyAskForHost();
            }

            if (pm == null){
                Logger.log(LoggerPriority.NOTIFICATION, "You are the first and the only executor");
                return;
            }

            for (InetAddress ia : pm.getKnownHosts() ){         // adding new got executor location to the list
                if (!kh.contains(ia)){
                    kh.add(ia);
                }
            }
        }

        this.sendTo(new JoinMessage(), kh);
    }

    public void send(Message msg){
        sendTo(msg, Executor.getIstance().getExecutorToNumberOfJobs().keySet());
    }

    public void sendTo(Message msg, Collection<InetAddress> recepient){
        for(InetAddress ia : recepient){
            try {
                if (ia.equals(ExecutorMain.localIP)){            // skipping myself if present
                    continue;
                }
                SocketSenderUnicast.send(msg, ia, this.port);
            } catch (Exception e){
                Logger.log(LoggerPriority.WARNING, "Error while contacting host: " + ia.toString() + ". Removing from active executors");
                Executor.getIstance().removeExecutor(ia);
                continue;
            }
        }
    }

    private void loadKnownExecutors(){
        Logger.log(LoggerPriority.NOTIFICATION, "Loading known host from file");
        File file = new File(System.getProperty("user.dir") + "/knownExecutors.txt");
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            ArrayList<InetAddress> kh = Executor.getIstance().getKnownExecutors();
            while (line != null){
                kh.add(InetAddress.getByName(line.substring(1)));
                line = br.readLine();
            }

        } catch (UnknownHostException e) {

        } catch (IOException e) {
            Logger.log(LoggerPriority.ERROR, "(not fatal) error while opening know host file");
            e.printStackTrace();
            Logger.log(LoggerPriority.WARNING, "Continuing with just the host known so far");
            return;
        }

    }
}
