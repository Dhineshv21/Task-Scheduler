package taskscli;

public class Task implements Runnable{

    private final String name;
    TaskScheduler scheduler;

    public Task (String name, TaskScheduler scheduler) {
        this.name = name;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        try {
            scheduler.markCompleted(name);
            scheduler.logHistory(name, "COMPLETED");
            System.out.println("Task is Executed: " + name);
        } catch (Exception e) {
            scheduler.markFailed(name); // new method
            scheduler.logHistory(name, "FAILED");
            System.out.println("Task failed: " + name);
        }
    }

    public String getName() {
        return name;
    }

}
