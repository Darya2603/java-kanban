package model;

public class Task {
    private static int idCounter = 0;
    protected int id;
    protected String name;
    protected String description;
    protected Status status;

    public Task(String name, String description) {
        this.id = ++idCounter;  // Генерация уникального ID
        this.name = name;
        this.description = description;
        this.status = Status.NEW; // По умолчанию статус NEW
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", name='" + name + "', description='" + description + "', status=" + status + "}";
    }
}



