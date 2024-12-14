package tracker.status;

public enum Status {
    NEW("Статус новой задачи"),
    IN_PROGRESS("Над задачей работают."),
    DONE("Задача выполнена.") {
        @Override
        public String getDescription() {
            return super.getDescription();
        }
    };

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}


