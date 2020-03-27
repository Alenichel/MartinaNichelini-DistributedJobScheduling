package main;

import Network.SocketSenderUnicast;

import java.io.IOException;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.math.BigDecimal;
import Interfaces.Compute;
import Tasks.Pi;

public class clientMain {
    public static void main(String args[]) {
        /*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/
        try {
            String name = "Compute";
            Registry registry = LocateRegistry.getRegistry("localhost");
            Compute comp = (Compute) registry.lookup(name);
            Pi task = new Pi(Integer.parseInt("100000"));
            BigDecimal pi = comp.executeTask(task);
            System.out.println(pi);
        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
        }
    }
}
