package task;

import java.time.*;

public class TaskSpec {
    private String name;
    ScheduleType scheduleType;
    LocalDateTime scheduledTime;
    Duration interval;
    Priority priority;
    Runnable action;
}
