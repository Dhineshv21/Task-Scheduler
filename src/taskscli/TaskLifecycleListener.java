package taskscli;

public interface TaskLifecycleListener {
    void onCompleted(String taskName);
    void onFailed(String taskName);
}
