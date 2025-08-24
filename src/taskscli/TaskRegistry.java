package taskscli;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TaskRegistry {
    private final Map<String, Task> taskMap = new HashMap<>();
    private final Map<String, TaskState> taskStates = new HashMap<>();
    private final Map<String, Integer> taskDelays = new HashMap<>();
    private final Map<String, LocalDateTime> scheduledTimes = new HashMap<>();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final TaskHistoryService historyService;

    public TaskRegistry(TaskHistoryService historyService) {
        this.historyService = historyService;
    }

    // 🆕 Register a new task
    public void registerTask(String name, Task task, int delay, LocalDateTime scheduledTime) {
        taskMap.put(name, task);
        taskStates.put(name, TaskState.SCHEDULED);
        taskDelays.put(name, delay);
        scheduledTimes.put(name, scheduledTime);
        historyService.logHistory(name, TaskState.SCHEDULED);
    }

    // 🔄 Update task state
    public void updateState(String name, TaskState newState) {
        if (taskMap.containsKey(name)) {
            taskStates.put(name, newState);
            historyService.logHistory(name, newState);
        } else {
            System.out.println("Cannot update state. Task '" + name + "' not found.");
        }
    }

    public void markCompleted(String name) {
        updateState(name, TaskState.COMPLETED);
    }

    public void markFailed(String name) {
        updateState(name, TaskState.FAILED);
    }

    // 📋 List all tasks
    public void listTasks() {
        if (taskMap.isEmpty()) {
            System.out.println("No tasks scheduled.");
            return;
        }

        System.out.println("Scheduled Tasks:");
        for (String name : taskMap.keySet()) {
            TaskState state = taskStates.getOrDefault(name, TaskState.UNKNOWN);
            System.out.printf("- %s [%s] %s%n", name, state.name(), state.getIcon());
        }
    }

    // ❌ Remove a task
    public void removeTask(String name) {
        taskMap.remove(name);
        taskStates.remove(name);
        taskDelays.remove(name);
        scheduledTimes.remove(name);
    }

    // 🕒 Get scheduled time
    public LocalDateTime getScheduledTime(String name) {
        return scheduledTimes.get(name);
    }

    // ⏱️ Update delay
    public void updateDelay(String name, int newDelay) {
        if (taskMap.containsKey(name)) {
            taskDelays.put(name, newDelay);
        } else {
            System.out.println("Cannot update delay. Task '" + name + "' not found.");
        }
    }

    // 🔍 Check existence
    public boolean containsTask(String name) {
        return taskMap.containsKey(name);
    }

    // ✏️ Rename task
    public void renameTask(String oldName, String newName) {
        if (!taskMap.containsKey(oldName)) {
            System.out.println("Task '" + oldName + "' not found.");
            return;
        }

        Task task = taskMap.remove(oldName);
        task.setName(newName);
        taskMap.put(newName, task);

        taskStates.put(newName, taskStates.remove(oldName));
        taskDelays.put(newName, taskDelays.remove(oldName));
        scheduledTimes.put(newName, scheduledTimes.remove(oldName));

        System.out.println("Task '" + oldName + "' renamed to '" + newName + "' successfully.");
    }

    // 📊 Get task status
    public void getStatus(String name) {
        TaskState state = taskStates.get(name);
        if (state != null) {
            System.out.printf("Status of '%s': %s %s%n", name, state.name(), state.getIcon());
        } else {
            System.out.printf("Task '%s' not found.%n", name);
        }
    }

    // 📅 Show scheduled time
    public void showSchedule(String name) {
        LocalDateTime time = scheduledTimes.get(name);
        if (time == null) {
            System.out.printf("No scheduled time found for task '%s'.%n", name);
            return;
        }

        String date = time.format(dateFormatter);
        String hour = time.format(timeFormatter);
        System.out.printf("Task '%s' is scheduled for %s at %s%n", name, date, hour);
    }

    // 🔧 Accessors
    public Task getTask(String name) {
        return taskMap.get(name);
    }

    public int getDelay(String name) {
        return taskDelays.getOrDefault(name, -1);
    }

    // 📜 History
    public void printHistory(String name) {
        historyService.printHistory(name);
    }

    public void clearHistory(String name) {
        historyService.clearHistory(name);
    }
}
