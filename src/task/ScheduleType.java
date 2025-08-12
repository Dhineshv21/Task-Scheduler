package task;

public enum ScheduleType {
    IMMEDIATE,      // Run now
    DELAYED,        // Run after delay
    FIXED_RATE,     // Repeat at fixed intervals
    RECURRING, FIXED_DELAY     // Repeat after each completion
}
