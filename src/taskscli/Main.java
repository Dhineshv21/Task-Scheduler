package taskscli;

import java.util.Scanner;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TaskScheduler scheduler = new TaskScheduler();

        System.out.println("Welcome to Task Scheduler");

        while(true) {
            System.out.println("\nEnter command: ADD <name> <description> <delay>, SHUTDOWN");
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
                    String description = parts[2];
                    int delay = Integer.parseInt(parts[3]);
                    scheduler.schedule(name, description, delay);
                    break;
                    
                case "LIST":
                    scheduler.listTask();
                    break;

                case "DELETE":
                    System.out.print("Enter task name to delete: ");
                    String nameToDelete = sc.nextLine();
                    scheduler.deleteTask(nameToDelete);
                    break;

                case "STATUS":
                    System.out.print("Enter task name to get Status: ");
                    String nameToGetStatus = sc.nextLine();
                    scheduler.getStatus(nameToGetStatus);
                    break;

                case "HISTORY":
                    scheduler.printHistory();
                    break;

                case "REPEAT":
                    System.out.println("\nREPEAT: <name> <description>");
                    String repeatInput = sc.nextLine().trim();
                    String[] repeatParts = repeatInput.split("\\s+", 2);

                    if (repeatParts.length < 2) {
                        System.out.println("Invalid input. Usage: REPEAT <name> <description>");
                        break;
                    }

                    System.out.print("Enter delay interval in seconds: ");

                    int repeatDelay = -1;
                    while (repeatDelay < 0) {
                        System.out.print("Enter delay in seconds: ");
                        String delayInput = sc.nextLine().trim();
                        try {
                            repeatDelay = Integer.parseInt(delayInput);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a valid number.");
                        }
                    }

                    String repeatName = repeatParts[0];
                    String repeatDesc = repeatParts[1];
                    scheduler.repeatingTask(repeatName, repeatDesc, repeatDelay);
                    break;

                case "STOPREPEAT":
                    System.out.print("Enter task name to stop repeating: ");
                    String stopName = sc.nextLine().trim();
                    scheduler.stopRepeatingTask(stopName);
                    break;

                case "CLEARHISTORY":
                    scheduler.clearHistory();
                    break;

                case "RENAME":
                    scheduler.listTask();
                    System.out.println("\nEnter command: RENAME <task_id> <new_task_name>");
                    String renameInput = sc.nextLine().trim();
                    String[] tokens = renameInput.split(" ");
                    if (tokens.length >= 3) {
                        String taskId = tokens[1];
                        String newName = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length)).replaceAll("^\"|\"$", "");
                        scheduler.renameTask(taskId, newName);
                    } else {
                        System.out.println("Usage: RENAME <task_id> <new_task_name>");
                    }
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
