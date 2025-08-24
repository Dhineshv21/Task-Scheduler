package taskscli;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class RepeatingTaskService implements TaskLifecycleListener {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final Map<String, ScheduledFuture<?>> repeatingTasks = new HashMap<>();
    private final Map<String, TaskState> taskStates = new HashMap<>();
    private final Map<String, Task> taskMap = new HashMap<>();
    private final TaskHistoryService historyService = new TaskHistoryService();

    public RepeatingTaskService(TaskRegistry registry, TaskHistoryService historyService) {
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
                    historyService.logHistory(taskName, TaskState.REPEATING);
                } catch (Exception e) {
                    taskStates.put(taskName, TaskState.FAILED);
                    historyService.logHistory(taskName, TaskState.FAILED);
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
            historyService.logHistory(name, TaskState.STOPPED);
            System.out.println("Repeating Task Stopped: " + name);
        } else {
            System.out.println("Failed to stop repeating task: " + name);
        }
    }

    @Override
    public void onCompleted(String taskName) {
        taskStates.put(taskName, TaskState.COMPLETED);
        historyService.logHistory(taskName, TaskState.COMPLETED);
    }

    @Override
    public void onFailed(String taskName) {
        taskStates.put(taskName, TaskState.FAILED);
        historyService.logHistory(taskName, TaskState.FAILED);
    }
}
