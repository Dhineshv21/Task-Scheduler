package taskscli;


public class Task implements Runnable {

    private String name;
    private final String taskDescription;
    private final TaskLifecycleListener listener;

    public Task(String name, String description, TaskLifecycleListener listener) {
        this.name = name;
        this.taskDescription = description;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            listener.onCompleted(name);
            System.out.println("Task is Executed: " + name);
        } catch (Exception e) {
            listener.onFailed(name);
            System.out.println("Task failed: " + name);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskDescription() {
        return taskDescription;
    }
}

