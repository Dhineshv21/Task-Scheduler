package taskscli;

public class Task implements Runnable{

    private String name;
    private final String taskDescription;
    TaskScheduler scheduler;

    public Task (String name, String description, TaskScheduler scheduler) {
        this.name = name;
        this.taskDescription = description;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        try {
            scheduler.markCompleted(name);
            scheduler.logHistory(name, "COMPLETED ✅ ");
            System.out.println("Task is Executed: " + name);
        } catch (Exception e) {
            scheduler.markFailed(name);
            scheduler.logHistory(name, "FAILED ❌");
            System.out.println("Task failed: " + name);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

}
