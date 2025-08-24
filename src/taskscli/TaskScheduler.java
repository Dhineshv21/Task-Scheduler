package taskscli;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.time.format.DateTimeFormatter;
import taskscli.TaskState;

public class TaskScheduler implements TaskLifecycleListener {

    private final Map<String, TaskState> taskStates = new HashMap<>();
    private final Map<String, List<String>> taskHistory = new HashMap<>();

    public void logHistory(String taskName, String statusMessage) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = "[" + timestamp + "] " + statusMessage;

        taskHistory.computeIfAbsent(taskName, k -> new ArrayList<>()).add(entry);
    }


    @Override
    public void onCompleted(String taskName) {
        markCompleted(taskName);
        logHistory(taskName, TaskState.COMPLETED.toString());

    }

    @Override
    public void onFailed(String taskName) {
        markFailed(taskName);
        logHistory(taskName, TaskState.FAILED.name() + " ❌");

    }

    public void markCompleted(String taskName) {
        taskStates.put(taskName, TaskState.COMPLETED);
    }

    public void markFailed(String taskName) {
        taskStates.put(taskName, TaskState.FAILED);
    }

    public void updateState(String taskName, TaskState state) {
        taskStates.put(taskName, state);
    }

    public TaskState getState(String taskName) {
        return taskStates.getOrDefault(taskName, null);
    }

    public void printAllStates() {
        taskStates.forEach((name, state) -> System.out.println(name + " → " + state));
    }

    public List<String> getHistory(String taskName) {
        return taskHistory.getOrDefault(taskName, new ArrayList<>());
    }

}
