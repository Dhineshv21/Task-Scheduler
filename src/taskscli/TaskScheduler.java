package taskscli;

import java.util.concurrent.*;

public class TaskScheduler {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public void schedule(String name, int delaySeconds) {
        Task task = new Task(name);
        executor.schedule   (task, delaySeconds, TimeUnit.SECONDS);
        System.out.println("Scheduled: " + name + " after " + delaySeconds + "s");
    }

    public void shutdown() {
        executor.shutdown();
        System.out.println("Scheduler Shutdown");
    }
}
