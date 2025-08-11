package task;

public enum TaskStatus {
    PENDING,        // Task created but not validated
    VALIDATING,     // Undergoing condition checks
    SCHEDULED,      // Scheduled for future execution
    QUEUED,         // Ready to run, waiting in thread pool
    RUNNING,        // Currently executing
    COMPLETED,      // Finished successfully
    FAILED,         // Threw exception or validation failed
    CANCELED        // Manually removed or aborted
}
