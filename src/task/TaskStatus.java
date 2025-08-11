package task;

public enum TaskStatus {
    PENDING,        // Task created but not validated
    RUNNING,        // Currently executing
    COMPLETED,      // Finished successfully
    FAILED,         // Threw exception or validation failed
    CANCELED        // Manually removed or aborted
}
