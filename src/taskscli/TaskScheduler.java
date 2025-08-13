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

    public void schedule(String name, int delaySeconds) {
        Task task = new Task(name, this);
        executor.schedule   (task, delaySeconds, TimeUnit.SECONDS);
        System.out.println("Scheduled: " + name + " after " + delaySeconds + "s");
        scheduledTaskNames.add(name);
        taskStatusMap.put(name, "PENDING");
    }

    public void listTask() {
        System.out.println("List of Tasks: ");
        for (String ls : scheduledTaskNames) {
            System.out.println("Task: " + ls);
        }
    }

    public void markCompleted(String name) {
        taskStatusMap.put(name, "COMPLETED");
    }

    public void shutdown() {
        executor.shutdown();
        System.out.println("Scheduler Shutdown");
    }
}
