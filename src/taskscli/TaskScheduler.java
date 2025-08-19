package taskscli;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.time.format.DateTimeFormatter;


public class TaskScheduler {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final List<String> scheduledTaskNames = new ArrayList<>();
    private final Map<String, String> taskStatusMap = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> repeatingTasks = new HashMap<>();
    private final Map<String, Task> taskMap = new HashMap<>();
    private final List<TaskHistoryEntry> history = new ArrayList<>();


    public void schedule(String name, String description, int delaySeconds) {
        Task task = new Task(name, description, this);
        ScheduledFuture<?> future = executor.schedule(task, delaySeconds, TimeUnit.SECONDS);
        scheduledTasks.put(name, future);
        taskMap.put(name, task);
        System.out.println("Scheduled: " + name + " after " + delaySeconds + "s");
        scheduledTaskNames.add(name);
        taskStatusMap.put(name, "PENDING ⌛");
    }

    public void listTask() {
        if (scheduledTaskNames.isEmpty()) {
            System.out.println("No tasks scheduled.");
            return;
        }
        System.out.println("List of Tasks: ");
        for (String name : scheduledTaskNames) {
            Task task = taskMap.get(name);
            System.out.printf("Task: %-15s | Desc: %s%n", name, task.getTaskDescription());
        }
    }

    public void printHistory() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        for (TaskHistoryEntry entry : history) {
            String date = entry.getTimestamp().format(dateFormatter);
            String time = entry.getTimestamp().format(timeFormatter);
            System.out.printf("Task: %-10s | Status: %-10s | Date: %s Time: %s%n" ,
                    entry.getTaskName(), entry.getStatus(), date, time);
        }

    }

    public void repeatingTask(String name, String description, int delay) {
        if (repeatingTasks.containsKey(name)) {
            System.out.println("Task '" + name + "' is already repeating.");
            return;
        }
        class RepeatingTasks implements Runnable {
            private final String taskName;

            public RepeatingTasks(String taskName) {
                this.taskName = taskName;
            }

            @Override
            public void run() {
                try {
                    System.out.println("Repeating Task Executed: " + taskName);
                    logHistory(name, "REPEATED \uD83D\uDD04");
                } catch (Exception e) {
                    markFailed(taskName);
                    System.out.println("Repeating Task Failed: " + taskName);
                }
            }
        }

        RepeatingTasks task = new RepeatingTasks(name);
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(task, delay, delay, TimeUnit.SECONDS);
        repeatingTasks.put(name, future);
        taskStatusMap.put(name, "REPEATING \uD83D\uDD04");
        taskMap.put(name, new Task(name, description, this));
        System.out.println("Repeating Task Scheduled: " + name + " every " + delay + "s");
    }

    public void stopRepeatingTask(String name) {
        ScheduledFuture<?> future = repeatingTasks.get(name);
        if (future == null) {
            System.out.println("No repeating task found with name: " + name);
            return;
        }

        boolean cancelled = future.cancel(false);
        if (cancelled) {
            repeatingTasks.remove(name);
            taskStatusMap.put(name, "STOPPED ⛔");
            logHistory(name, "STOPPED ⛔");
            System.out.println("Repeating Task Stopped: " + name);
        } else {
            System.out.println("Failed to stop repeating task: " + name);
        }
    }

    public void logHistory(String taskName, String status) {
        history.add(new TaskHistoryEntry(taskName, status, LocalDateTime.now()));
    }


    public void markCompleted(String name) {
        taskStatusMap.put(name, "COMPLETED ✅ ");
    }

    public void getStatus(String name) {
        if (taskStatusMap.containsKey(name)) {
            String status = taskStatusMap.get(name);
            String type = repeatingTasks.containsKey(name) ? "REPEATING" : "ONE-TIME";
            System.out.println("Status of '" + name + "': " + status + " (" + type + ")");
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
            taskStatusMap.put(name, "DELETED \uD83D\uDDD1\uFE0F ");
            history.add(new TaskHistoryEntry(name, "DELETED \uD83D\uDDD1\uFE0F ", LocalDateTime.now()));
            System.out.println("Task Removed: " + name);
        } else {
            System.out.println("Failed to remove Task");
        }
    }

    public void markFailed(String name) {
        taskStatusMap.put(name, "FAILED ❌ ");
        history.add(new TaskHistoryEntry(name, "FAILED ❌ ", LocalDateTime.now()));
    }

    public void clearHistory() {

    }

    public void shutdown() {
        executor.shutdown();
        System.out.println("The Program has ended.");
    }
}