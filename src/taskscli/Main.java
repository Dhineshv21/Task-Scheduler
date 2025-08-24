package taskscli;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TaskServiceCoordinator scheduler = new TaskServiceCoordinator();

        System.out.println("🛠️ Welcome to Task Scheduler CLI");

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add Task");
            System.out.println("2. List Tasks");
            System.out.println("3. Delete Task");
            System.out.println("4. Get Task Status");
            System.out.println("5. View Task History");
            System.out.println("6. Clear Task History");
            System.out.println("7. Schedule Repeating Task");
            System.out.println("8. Stop Repeating Task");
            System.out.println("9. Rename Task");
            System.out.println("10. Shutdown");

            System.out.print("\nEnter choice (1-10): ");
            String input = sc.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 10.");
                continue;
            }

            switch (choice) {
                case 1: // Add Task
                    System.out.print("Enter task name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Enter description: ");
                    String desc = sc.nextLine().trim();
                    System.out.print("Enter delay in seconds: ");
                    int delay = Integer.parseInt(sc.nextLine().trim());
                    scheduler.addTask(name, desc, delay);
                    break;

                case 2: // List Tasks
                    scheduler.listTasks();
                    break;

                case 3: // Delete Task
                    System.out.print("Enter task name to delete: ");
                    String deleteName = sc.nextLine().trim();
                    scheduler.removeTask(deleteName);
                    break;

                case 4: // Get Status
                    System.out.print("Enter task name to check status: ");
                    String statusName = sc.nextLine().trim();
                    scheduler.getStatus(statusName);
                    break;

                case 5: // View History
                    System.out.print("Enter task name to view history: ");
                    String historyName = sc.nextLine().trim();
                    scheduler.printHistory(historyName);
                    break;

                case 6: // Clear History
                    System.out.print("Enter task name to clear history: ");
                    String clearName = sc.nextLine().trim();
                    scheduler.clearHistory(clearName);
                    break;

                case 7: // Repeating Task
                    System.out.print("Enter task name: ");
                    String repeatName = sc.nextLine().trim();
                    System.out.print("Enter description: ");
                    String repeatDesc = sc.nextLine().trim();
                    System.out.print("Enter delay interval in seconds: ");
                    int repeatDelay = Integer.parseInt(sc.nextLine().trim());
                    scheduler.repeatTask(repeatName, repeatDesc, repeatDelay);
                    break;

                case 8: // Stop Repeating
                    System.out.print("Enter task name to stop repeating: ");
                    String stopName = sc.nextLine().trim();
                    scheduler.stopRepeat(stopName);
                    break;

                case 9: // Rename Task
                    scheduler.listTasks();
                    System.out.print("Enter current task name: ");
                    String oldName = sc.nextLine().trim();
                    System.out.print("Enter new task name: ");
                    String newName = sc.nextLine().trim();
                    scheduler.renameTask(oldName, newName);
                    break;

                case 10: // Shutdown
                    scheduler.shutdown();
                    System.out.println("👋 Scheduler shutdown complete.");
                    return;

                default:
                    System.out.println("Invalid choice. Please select a number between 1 and 10.");
            }
        }
    }
}
