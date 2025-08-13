package taskscli;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TaskScheduler scheduler = new TaskScheduler();

        System.out.println("Welcome to Task Scheduler");

        while(true) {
            System.out.println("\nEnter command: ADD <name> <delay>, SHUTDOWN");
            String input = sc.nextLine().trim();
            String[] parts = input.split("\\s+");

            if(parts.length == 0) continue;

            String command = parts[0].toUpperCase();

            switch (command) {
                case "ADD":
                    if(parts.length < 3) {
                        System.out.println("Invalid Input. Eg. ADD <name> <delay>");
                        break;
                    }
                    String name = parts[1];
                    int delay = Integer.parseInt(parts[2]);
                    scheduler.schedule(name, delay);
                    break;

                case "LIST":
                    scheduler.listTask();
                    break;

                case "UPDATE":
                    scheduler.markCompleted(parts[1]);
                    break;

                case "SHUTDOWN":
                    scheduler.shutdown();
                    return;

                default:
                    System.out.println("Unknown Command.");
            }
        }
    }
}
