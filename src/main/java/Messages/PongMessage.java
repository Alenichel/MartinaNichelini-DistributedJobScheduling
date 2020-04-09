package Messages;

import Enumeration.MessageType;

import java.net.InetAddress;
import java.util.Collection;

public class PongMessage extends Message {

    private Integer numberOfJobs;
    private Collection<InetAddress> knownHosts;

    public PongMessage(Integer numberOfJobs, Collection<InetAddress> knowHosts) {
        this.type = MessageType.PONG_MESSAGE;
        this.numberOfJobs = numberOfJobs;
        this.knownHosts = knowHosts;
    }

    public Integer getNumberOfJobs() {
        return numberOfJobs;
    }

    public Collection<InetAddress> getKnownHosts() {
        return knownHosts;
    }
}
