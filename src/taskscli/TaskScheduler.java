package taskscli;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.time.format.DateTimeFormatter;

enum TaskState {
    PENDING("⌛"),
    RUNNING("▶️"),
    PAUSED("⏸"),
    COMPLETED("✅"),
    FAILED("❌"),
    DELETED("🗑️"),
    STOPPED("⛔"),
    REPEATING("🔁"),
    RESCHEDULED("⏳");

    private final String icon;
    TaskState(String icon) { this.icon = icon; }
    public String getIcon() { return icon; }
}



public class TaskScheduler {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final List<String> scheduledTaskNames = new ArrayList<>();
    private final Map<String, TaskState> taskStates = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> repeatingTasks = new HashMap<>();
    private final Map<String, Task> taskMap = new HashMap<>();
    private final Map<String, Integer> taskDelay = new HashMap<>();
    private final Map<String, LocalDateTime> scheduledTimeMap = new HashMap<>();
    private final List<TaskHistoryEntry> history = new ArrayList<>();
    private final Map<String, Integer> remainingDelayMap = new HashMap<>();
    private final Map<String, Boolean> pausedTasks = new HashMap<>();

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");


    public void schedule(String name, String description, int delaySeconds) {
        Task task = new Task(name, description, this);
        ScheduledFuture<?> future = executor.schedule(task, delaySeconds, TimeUnit.SECONDS);
        scheduledTasks.put(name, future);
        taskDelay.put(name, delaySeconds);
        taskMap.put(name, task);
        System.out.println("Scheduled: " + name + " after " + delaySeconds + "s");
        scheduledTimeMap.put(name, LocalDateTime.now());
        scheduledTaskNames.add(name);
        taskStates.put(name, TaskState.PENDING);
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

    public void renameTask(String oldName, String newName) {
        Task task = taskMap.get(oldName);
        if(task != null) {
            task.setName(newName);
            System.out.println("Task " + oldName + " renamed to \"" + newName + "\" successfully.");
        } else {
            System.out.println("Task Not Found");
        }
    }

    public void printHistory() {
        if(history.isEmpty()) {
            System.out.println("No History Found");
            return;
        }

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
                    logHistory(name, TaskState.REPEATING);
                } catch (Exception e) {
                    markFailed(taskName);
                    System.out.println("Repeating Task Failed: " + taskName);
                }
            }
        }

        RepeatingTasks task = new RepeatingTasks(name);
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(task, delay, delay, TimeUnit.SECONDS);
        repeatingTasks.put(name, future);
        taskStates.put(name, TaskState.REPEATING);
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
            taskStates.put(name, TaskState.STOPPED);
            logHistory(name, TaskState.STOPPED);
            System.out.println("Repeating Task Stopped: " + name);
        } else {
            System.out.println("Failed to stop repeating task: " + name);
        }
    }

    public void logHistory(String taskName, TaskState state) {
        String status = state.name() + " " + state.getIcon();
        history.add(new TaskHistoryEntry(taskName, status, LocalDateTime.now()));
    }



    public void markCompleted(String name) {
        taskStates.put(name, TaskState.COMPLETED);
    }

    public void getStatus(String name) {
        if (taskStates.containsKey(name)) {
            TaskState state = taskStates.get(name);
            System.out.println("Status of '" + name + "': " + state + " " + state.getIcon());
        } else {
            System.out.println("Task: '" + name + "' not found");
        }
    }

    public void deleteSpecificTask(String name) {
        for (int i = 0; i < history.size(); i++) {
            if(history.get(i).getTaskName().equals(name)) {
                history.remove(i);
                System.out.println("Task History: " + name + " has been removed successfully.");
                return;
            }
        }
        System.out.println("Task not found.");
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
            taskStates.put(name, TaskState.DELETED);
            history.add(new TaskHistoryEntry(name, "DELETED \uD83D\uDDD1\uFE0F ", LocalDateTime.now()));
            System.out.println("Task Removed: " + name);
        } else {
            System.out.println("Failed to remove Task");
        }
    }

    public void markFailed(String name) {
        updateState(name, TaskState.FAILED);
    }


    public void clearHistory() {
        if(history != null) {
            history.clear();
            System.out.println("History Has Been Cleared Successfully");
        } else {
            System.out.println("No History to clear");
        }
    }

    public void increaseDelay(String name, int delay) {
        if (delay <= 0){
            System.out.println("Enter a Valid Delay Time");
            return;
        }
        if (!taskDelay.containsKey(name)) {
            System.out.println("Original delay not found for task '" + name + "'.");
            return;
        }
        if(taskStates.get(name) == TaskState.PENDING) {
            ScheduledFuture<?> future = scheduledTasks.get(name);
            if (future != null && !future.isDone()) {
                future.cancel(false);
            }
            int originalDelay = taskDelay.get(name);
            int newDelay = originalDelay + delay;
            taskDelay.put(name, newDelay);

            Task task = taskMap.get(name);
            ScheduledFuture<?> newFuture = executor.schedule(task, newDelay, TimeUnit.SECONDS);
            scheduledTasks.put(name, newFuture);
            updateState(name, TaskState.RESCHEDULED);
            scheduledTimeMap.put(name, LocalDateTime.now().plusSeconds(newDelay));
            System.out.println("Task '" + name + "' rescheduled to run in " + newDelay + " seconds.");
        }   else {
            System.out.println("Task '" + name + "' is not eligible for delay increase.");
        }
    }

    public void showSchedule(String name) {
        if (!scheduledTimeMap.containsKey(name)) {
            System.out.println("No scheduled time found for task '" + name + "'.");
            return;
        }

        LocalDateTime scheduledTime = scheduledTimeMap.get(name);
        String date = scheduledTime.format(dateFormatter);
        String time = scheduledTime.format(timeFormatter);

        System.out.printf("Task '%s' is scheduled for %s at %s%n", name, date, time);
    }

    public void pauseTask(String name) {
        if (!scheduledTasks.containsKey(name)) {
            System.out.println("Task '" + name + "' not found.");
            return;
        }

        LocalDateTime scheduledTime = scheduledTimeMap.get(name);
        if (scheduledTime == null) {
            System.out.println("Scheduled time not found for task '" + name + "'.");
            return;
        }

        ScheduledFuture<?> future = scheduledTasks.get(name);
        if (future.isDone()) {
            System.out.println("Task '" + name + "' has already completed.");
            return;
        }

        boolean cancelled = future.cancel(false);
        if (cancelled) {
            long elapsed = Duration.between(scheduledTimeMap.get(name), LocalDateTime.now()).getSeconds();
            int originalDelay = taskDelay.get(name);
            int remaining = Math.max(0, originalDelay - (int) elapsed);

            remainingDelayMap.put(name, remaining);
            pausedTasks.put(name, true);
            updateState(name, TaskState.PAUSED);
            System.out.println("Task '" + name + "' paused with " + remaining + " seconds remaining.");
        } else {
            System.out.println("Failed to pause task '" + name + "'.");
        }
    }

    private void updateState(String name, TaskState state) {
        taskStates.put(name, state);
        logHistory(name, state);
    }


    public void resumeTask(String name) {
        if (!pausedTasks.containsKey(name) || !pausedTasks.get(name)) {
            System.out.println("Task '" + name + "' is not paused.");
            return;
        }

        int remaining = remainingDelayMap.getOrDefault(name, -1);
        if (remaining <= 0) {
            System.out.println("No remaining delay found for task '" + name + "'.");
            return;
        }

        Task task = taskMap.get(name);
        ScheduledFuture<?> future = executor.schedule(task, remaining, TimeUnit.SECONDS);
        scheduledTasks.put(name, future);
        taskDelay.put(name, remaining);
        scheduledTimeMap.put(name, LocalDateTime.now().plusSeconds(remaining));

        pausedTasks.remove(name);
        remainingDelayMap.remove(name);
        updateState(name, TaskState.RUNNING);
        System.out.println("Task '" + name + "' resumed and will run in " + remaining + " seconds.");
    }


    public void shutdown() {
        executor.shutdown();
        System.out.println("The Program has ended.");
    }
}