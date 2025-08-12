package scheduler;

import task.ScheduleType;
import task.Task;
import task.TaskSpec;
import task.TaskStatus;

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

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void shutdown() {
        running = false;
        workerPool.shutdown();
    }

    private class TaskChecker implements Runnable {
        @Override
        public void run() {
            while (running) {
                for (Task task : tasks) {
                    if(LocalDateTime.now().isAfter(task.getNextRunTime()) && task.getStatus() == TaskStatus.PENDING) {
                        task.setStatus(TaskStatus.RUNNING);
                        workerPool.submit(new TaskRunner(task));
                    }
                    if (task.getNextRunTime() == null) {
                        System.err.println("⚠️ Task " + task.getSpec().getName() + " has null nextRunTime");
                        continue;
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }
            }
        }

        private class TaskRunner implements Runnable {
            private Task task;

            TaskRunner(Task task) {
                this.task = task;
            }

            @Override
            public void run() {
                try {
                    task.getSpec().getAction().run();
                    task.setStatus(TaskStatus.COMPLETED);
                } catch (Exception e) {
                    task.setStatus(TaskStatus.FAILED);
                }

                if (task.getSpec().getScheduleType() == ScheduleType.REPEATED) {
                    task.updateNextRunTime();
                    task.setStatus(TaskStatus.PENDING);
                }

                Runnable action = task.getSpec().getAction();
                if (action != null) {
                    action.run();
                } else {
                    System.err.println("⚠️ Task action is null for: " + task.getSpec().getName());
                }
                System.out.println("🔄 Running task: " + task.getSpec().getName() + " at " + LocalDateTime.now());

            }
        }
    }