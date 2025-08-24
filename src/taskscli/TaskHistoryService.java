package taskscli;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TaskHistoryService {
    private final Map<String, List<TaskHistoryEntry>> historyMap = new HashMap<>();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void logHistory(String taskName, TaskState state) {
        String status = state.name() + " " + state.getIcon();
        TaskHistoryEntry entry = new TaskHistoryEntry(taskName, status, LocalDateTime.now());
        historyMap.computeIfAbsent(taskName, k -> new ArrayList<>()).add(entry);
    }

    public void printHistory(String taskName) {
        List<TaskHistoryEntry> history = historyMap.get(taskName);
        if (history == null || history.isEmpty()) {
            System.out.println("No history found for task '" + taskName + "'.");
            return;
        }

        System.out.println("History for task '" + taskName + "':");
        for (TaskHistoryEntry entry : history) {
            String date = entry.getTimestamp().format(dateFormatter);
            String time = entry.getTimestamp().format(timeFormatter);
            System.out.printf("  - Status: %-15s | Date: %s | Time: %s%n",
                    entry.getStatus(), date, time);
        }
    }


    public void clearHistory(String taskName) {
        if (historyMap.containsKey(taskName)) {
            historyMap.remove(taskName);
            System.out.println("History cleared for task '" + taskName + "'.");
        } else {
            System.out.println("No history to clear for task '" + taskName + "'.");
        }
    }
}
