package task;

import java.time.LocalDateTime;

public class Task {
    TaskSpec spec;
    TaskStatus status;
    private LocalDateTime nextRunTime;

    public TaskSpec getSpec() {
        return spec;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getNextRunTime() {
        return nextRunTime;
    }

    public void updateNextRunTime() {
        if (spec.getScheduleType() == ScheduleType.REPEATED) {
            this.nextRunTime = LocalDateTime.now().plus(spec.getInterval());
        }
    }


    public Task(TaskSpec task) {
        this.spec = task;
    }
}
