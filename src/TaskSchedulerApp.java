
import scheduler.*;
import task.*;
import java.time.*;


public class TaskSchedulerApp {
    public static void main(String[] args) {
        TaskScheduler scheduler = new TaskScheduler();

        TaskSpec spec = new TaskSpec(
                "HelloTask",
                ScheduleType.RECURRING,
                LocalDateTime.now().plusSeconds(5),
                Duration.ofSeconds(10),
                Priority.MEDIUM,
                () -> System.out.println("Hello from task scheduled" + LocalDateTime.now())
        );

        Task task = new Task(spec);
        scheduler.addTask(task.getSpec());
        scheduler.start();

        try {
            Thread.sleep(3000); // Let it run for 30 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduler.shutdown();

    }
}
