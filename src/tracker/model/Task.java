package tracker.model;

import tracker.status.Status;
import tracker.status.TaskType;

import java.util.Objects;

public class Task {
    protected String nameTask;
    protected String descriptionTask;
    protected int id;
    protected Status status = Status.NEW;
    private int epicId;
    protected TaskType taskType;

    public Task() {
    }

    public Task(String nameTask, String descriptionTask, Status status) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
        this.taskType = TaskType.TASK;
    }

    public Task(int id, String nameTask, String descriptionTask, Status status) {
        this.id = id;
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
        this.taskType = TaskType.TASK;
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public String getDescriptionTask() {
        return descriptionTask;
    }

    public void setDescriptionTask(String descriptionTask) {
        this.descriptionTask = descriptionTask;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nameTask;
    }
}



