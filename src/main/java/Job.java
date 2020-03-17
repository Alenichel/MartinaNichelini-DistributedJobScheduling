import java.util.UUID;

public class Job {
    private String jobID;
    private String clientID;
    private String executorID;

    public Job(String clientID){
        this.jobID = UUID.randomUUID().toString();
        this.clientID = clientID;
    }
}
