package taskscli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class TaskScheduler {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final List<String> scheduledTaskNames = new ArrayList<>();
    private final Map<String, String> taskStatusMap = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    public void schedule(String name, int delaySeconds) {
        Task task = new Task(name, this);
        ScheduledFuture<?> future = executor.schedule(task, delaySeconds, TimeUnit.SECONDS);
        scheduledTasks.put(name, future);
        System.out.println("Scheduled: " + name + " after " + delaySeconds + "s");
        scheduledTaskNames.add(name);
        taskStatusMap.put(name, "PENDING");
    }

    public void listTask() {
        if (scheduledTaskNames.isEmpty()) {
            System.out.println("No tasks scheduled.");
            return;
        }
        System.out.println("List of Tasks: ");
        for (String ls : scheduledTaskNames) {
            System.out.println("Task: " + ls);
        }
    }

    public void markCompleted(String name) {
        taskStatusMap.put(name, "COMPLETED");
    }

    public void getStatus(String name) {
        if(taskStatusMap.containsKey(name)) {
            System.out.println("Status of '" + name + "': " + taskStatusMap.get(name));
        } else {
            System.out.println("Task: '" + name + "' not found");
        }
    }

    public void deleteTask(String name) {
        ScheduledFuture<?> future = scheduledTasks.get(name);
        if (future == null) {
            System.out.println("No Task found");
            return;
        }
        boolean cancelled = future.cancel(false);
        if (cancelled) {
            scheduledTasks.remove(name);
            taskStatusMap.put(name, "DELETED");
            System.out.println("Task Removed: " + name);
        } else {
            System.out.println("Failed to remove Task");
        }
    }

    public void shutdown() {
        executor.shutdown();
        System.out.println("Scheduler Shutdown");
    }
}