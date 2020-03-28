package Main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.math.BigDecimal;
import Tasks.Compute;
import Tasks.Pi;

public class ClientMain {
    public static void main(String args[]) {
        /*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/
        try {
            String name = "Compute";
            Registry registry = LocateRegistry.getRegistry("localhost");        //TODO
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
