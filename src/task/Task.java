package task;

import java.time.LocalDateTime;

public class Task {
    TaskSpec spec;
    TaskStatus status;
    private LocalDateTime nextRunTime;

    public Task(TaskSpec task) {
        this.spec = task;
    }
}
