package Messages;

import Enumeration.MessageType;
import utils.NetworkUtilis;

import java.net.InetAddress;
import java.util.Collection;

public class PongMessage extends Message {

    private Integer numberOfJobs;
    private Collection<InetAddress> knownHosts;

    public PongMessage(Integer numberOfJobs, Collection<InetAddress> knowHosts, Boolean toAdjust) {
        this.type = MessageType.PONG_MESSAGE;
        this.numberOfJobs = numberOfJobs;
        this.knownHosts = knowHosts;
        if (toAdjust){
            adjustAddresses();
        }
    }

    private void adjustAddresses(){
        for(InetAddress ia : knownHosts){
            if (ia.equals(NetworkUtilis.getLocalAddress())){
                knownHosts.remove(ia);
                knownHosts.add(NetworkUtilis.getExternalAddress());
            }
        }
    }

    public Integer getNumberOfJobs() {
        return numberOfJobs;
    }

    public Collection<InetAddress> getKnownHosts() {
        return knownHosts;
    }
}
