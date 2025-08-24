package taskscli;

public class TaskMetadata {
    private TaskState state;
    private final long scheduledTime;

    public TaskMetadata(TaskState state, long scheduledTime) {
        this.state = state;
        this.scheduledTime = scheduledTime;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public long getScheduledTime() {
        return scheduledTime;
    }
}
