package taskscli;

public class Task implements Runnable{

    private String name;

    public Task (String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("Task is Executed: " + name);
    }

    public String getName() {
        return name;
    }
}
