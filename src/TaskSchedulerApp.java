
import scheduler.*;
import task.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TaskSchedulerApp {
    public static void main(String[] args) {
        TaskScheduler scheduler = new TaskScheduler();

        TaskSpec spec = new TaskSpec(
                "HelloTask",
                ScheduleType.REPEATED,
                LocalDateTime.now().plusSeconds(5),
                Duration.ofSeconds(10),
                Priority.MEDIUM,
                () -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
                    String formattedTime = LocalDateTime.now().format(formatter);
                    System.out.println("✅ Hello from task scheduled at " + formattedTime);
                }
        );

        Task task = new Task(spec);
        scheduler.addTask(task);
        scheduler.start();

        try {
            Thread.sleep(30000); // Let it run for 30 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduler.shutdown();

    }
}
