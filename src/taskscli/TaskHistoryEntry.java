package taskscli;

import java.time.LocalDateTime;

public class TaskHistoryEntry {
    private final String taskName;
    private final String status;
    private final LocalDateTime timestamp;

    public TaskHistoryEntry(String taskName, String status, LocalDateTime timestamp) {
        this.taskName = taskName;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
