package taskscli;

enum TaskState {
    PENDING("⌛"),
    RUNNING("▶️"),
    PAUSED("⏸"),
    COMPLETED("✅"),
    FAILED("❌"),
    DELETED("🗑️"),
    STOPPED("⛔"),
    REPEATING("🔁"),
    RESCHEDULED("⏳"),
    SCHEDULED("\uD83D\uDCC5"),
    UNKNOWN("❓");

    private final String icon;

    TaskState(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == STOPPED;
    }

    @Override
    public String toString() {
        return icon;
    }
}