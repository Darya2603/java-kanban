package tracker.status;

public enum Status {
    NEW("Задача создана."),
    IN_PROGRESS("Над задачей ведётся работа."),
    DONE("Задача выполнена.");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}


