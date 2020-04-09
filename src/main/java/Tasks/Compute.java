package Tasks;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Compute extends Remote {
    <T> String executeTask(Task<T> t) throws RemoteException;
}
