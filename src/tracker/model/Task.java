package tracker.model;

import tracker.status.Status;

public class Task {
    private int id;
    private final String name;
    private final String description;
    private Status status;
    private int epicId;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", name='" + name + "', description='" + description + "', status=" + status + '}';
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setDescription(String ignoredModifiedDescription) {
    }
}