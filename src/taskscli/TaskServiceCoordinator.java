package taskscli;

public class TaskServiceCoordinator {
    private final TaskRegistry registry;
    private final TaskHistoryService historyService;
    private final TaskManager manager;
    private final RepeatingTaskService repeatingService;

    public TaskServiceCoordinator() {
        this.historyService = new TaskHistoryService();
        this.registry = new TaskRegistry(historyService);
        this.manager = new TaskManager(registry, historyService);
        this.repeatingService = new RepeatingTaskService(registry, historyService);
    }

    public void addTask(String name, String desc, int delay) {
        manager.schedule(name, desc, delay);
    }

    public void repeatTask(String name, String desc, int delay) {
        repeatingService.repeatingTask(name, desc, delay);
    }

    public void stopRepeat(String name) {
        repeatingService.stopRepeatingTask(name);
    }

    public void listTasks() {
        registry.listTasks();
    }

    public void getStatus(String name) {
        registry.getStatus(name);
    }

    public void printHistory(String name) {
        historyService.printHistory(name);
    }

    public void clearHistory(String name) {
        historyService.clearHistory(name);
    }

    public void renameTask(String oldName, String newName) {
        registry.renameTask(oldName, newName);
    }

    public void removeTask(String name) {
        registry.removeTask(name);
    }


    public void shutdown() {
        manager.shutdown();
    }
}
