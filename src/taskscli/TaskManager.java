package taskscli;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class TaskManager implements TaskLifecycleListener{

    @Override
    public void onCompleted(String taskName) {
        registry.updateState(taskName, TaskState.COMPLETED);
        System.out.println("Task '" + taskName + "' completed ✅");
    }

    @Override
    public void onFailed(String taskName) {
        registry.updateState(taskName, TaskState.FAILED);
        System.out.println("Task '" + taskName + "' failed ❌");
    }


    private final TaskRegistry registry;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
    private final Map<String, Boolean> pausedTasks = new HashMap<>();
    private final Map<String, Integer> remainingDelayMap = new HashMap<>();

    public TaskManager(TaskRegistry registry, TaskHistoryService historyService) {
        this.registry = registry;
    }

    public void schedule(String name, String description, int delaySeconds) {
        Task task = new Task(name, description, this);
        ScheduledFuture<?> future = executor.schedule(task, delaySeconds, TimeUnit.SECONDS);
        scheduledTasks.put(name, future);
        registry.registerTask(name, task, delaySeconds, LocalDateTime.now());
        registry.updateState(name, TaskState.SCHEDULED);
        System.out.println("Scheduled: " + name + " after " + delaySeconds + "s");
    }

    public void pauseTask(String name) {
        ScheduledFuture<?> future = scheduledTasks.get(name);
        if (future == null) {
            System.out.println("Task '" + name + "' not found.");
            return;
        }

        if (future.isDone()) {
            System.out.println("Task '" + name + "' has already completed.");
            return;
        }

        LocalDateTime scheduledTime = registry.getScheduledTime(name);
        if (scheduledTime == null) {
            System.out.println("Scheduled time not found for task '" + name + "'.");
            return;
        }

        boolean cancelled = future.cancel(false);
        if (cancelled) {
            long elapsed = Duration.between(scheduledTime, LocalDateTime.now()).getSeconds();
            int originalDelay = registry.getDelay(name);
            int remaining = Math.max(0, originalDelay - (int) elapsed);

            remainingDelayMap.put(name, remaining);
            pausedTasks.put(name, true);
            registry.updateState(name, TaskState.PAUSED);
            System.out.println("Task '" + name + "' paused with " + remaining + " seconds remaining.");
        } else {
            System.out.println("Failed to pause task '" + name + "'.");
        }
    }

    public void increaseDelay(String name, int delay) {
        if (delay <= 0) {
            System.out.println("Enter a valid delay time.");
            return;
        }

        ScheduledFuture<?> future = scheduledTasks.get(name);
        if (future == null || future.isDone()) {
            System.out.println("Task '" + name + "' is not eligible for delay increase.");
            return;
        }

        int originalDelay = registry.getDelay(name);
        int newDelay = originalDelay + delay;

        future.cancel(false);
        Task task = registry.getTask(name);
        ScheduledFuture<?> newFuture = executor.schedule(task, newDelay, TimeUnit.SECONDS);
        scheduledTasks.put(name, newFuture);
        registry.updateDelay(name, newDelay);
        registry.updateState(name, TaskState.RESCHEDULED);
        System.out.println("Task '" + name + "' rescheduled to run in " + newDelay + " seconds.");
    }

    public void resumeTask(String name) {
        if (!pausedTasks.getOrDefault(name, false)) {
            System.out.println("Task '" + name + "' is not paused.");
            return;
        }

        int remaining = remainingDelayMap.getOrDefault(name, -1);
        if (remaining <= 0) {
            System.out.println("No remaining delay found for task '" + name + "'.");
            return;
        }

        Task task = registry.getTask(name);
        ScheduledFuture<?> future = executor.schedule(task, remaining, TimeUnit.SECONDS);
        scheduledTasks.put(name, future);
        registry.updateDelay(name, remaining);
        registry.updateState(name, TaskState.RUNNING);

        pausedTasks.remove(name);
        remainingDelayMap.remove(name);
        System.out.println("Task '" + name + "' resumed and will run in " + remaining + " seconds.");
    }

    public void shutdown() {
        executor.shutdownNow();
        System.out.println("The program has ended.");
    }
}
