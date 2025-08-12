package task;

import java.time.*;

public class TaskSpec {
    private String name;
    ScheduleType scheduleType;
    LocalDateTime scheduledTime;
    Duration interval;
    Priority priority;
    Runnable action;

    public String getName() {
        return name;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public Duration getInterval() {
        return interval;
    }

    public Priority getPriority() {
        return priority;
    }

    public Runnable getAction() {
        return action;
    }

    public boolean isRecurring() {
        return scheduleType == ScheduleType.RECURRING;
    }
}

