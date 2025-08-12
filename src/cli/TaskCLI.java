package cli;

import scheduler.*;
import task.Task;
import task.TaskSpec;

import java.util.Scanner;

public class TaskCLI {
    static Scanner sc = new Scanner(System.in);

    TaskScheduler scheduler = new TaskScheduler();

    protected void displayDetails() {
        System.out.println("Which Action you want to perform? ");
        System.out.println("""
                ADD,
                    SCHEDULE,
                    LIST,
                    STATUS,
                    DELETE,
                    PAUSE,
                    RESUME,
                    SHUTDOWN,
                    HELP""");
    }

    TaskCLI() {
        System.out.println("Welcome to Task Scheduler.");
        boolean isTrue = true;
        while (isTrue) {
            displayDetails();
            String userInput = sc.next();

        }
    }
}
