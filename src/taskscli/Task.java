package taskscli;


public class Task implements Runnable{

    private String name;
    TaskScheduler scheduler;

    public Task (String name, TaskScheduler scheduler) {
        this.name = name;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        scheduler.markCompleted(name);
        System.out.println("Task is Executed: " + name);
    }

    public String getName() {
        return name;
    }
}
