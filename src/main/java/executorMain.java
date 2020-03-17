import Network.SocketManager;

public class executorMain {

    public static void main(String[] args) {
        System.out.println("Hello World");
        SocketManager sm = new SocketManager();
        sm.start();
    }

}
