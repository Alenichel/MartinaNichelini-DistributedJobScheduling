package Messages;

import Enumeration.JobStatus;
import Enumeration.MessageType;
import Enumeration.TaskType;

public class ResultResponseMessage extends Message {

    private JobStatus jobStatus;
    private Object result;
    private TaskType taskType;

    public ResultResponseMessage(JobStatus jobStatus, Object result /*, TaskType taskType*/) {
        this.type = MessageType.RESULT_RESPONSE_MESSAGE;
        this.jobStatus = jobStatus;
        this.result = result;
        /*this.taskType = taskType;*/
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public Object getResult() {
        return result;
    }

    public TaskType getTaskType() {
        return taskType;
    }
}
