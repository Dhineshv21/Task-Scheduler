package scheduler;

import task.Task;
import task.TaskSpec;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TaskScheduler{
    List<Task> tasks = new ArrayList<>();
    private ExecutorService workerPool = Executors.newFixedThreadPool(4);
    private boolean running = true;

    public void start() {
        Thread checkerThread = new Thread(new TaskChecker());
        checkerThread.start();
    }

    public void addTask(TaskSpec task) {
        tasks.add(new Task(task));
    }

    public void shutdown() {
        running = false;
        workerPool.shutdown();
    }

    private class TaskChecker implements Runnable {

        @Override
        public void run() {

            }
        }
    }

