package taskscli;

import java.time.LocalDateTime;

public class TaskHistoryEntry {

    private final String name;
    private final String status;
    private final LocalDateTime timestamp;

    public TaskHistoryEntry(String name, String status, LocalDateTime timestamp) {
        this.name = name;
        this.status = status;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + name + " - " + status;
    }

    public String getTaskName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
