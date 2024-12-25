package tracker.model;

import tracker.status.Status;
import tracker.status.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String nameTask;
    protected String descriptionTask;
    protected int id;
    protected Status status = Status.NEW;
    private int epicId;
    protected TaskType taskType;

    // Новые поля
    private Duration duration; // продолжительность задачи
    private LocalDateTime startTime; // дата начала задачи

    public Task() {
    }

    public Task(String nameTask, String descriptionTask, Status status, Duration duration, LocalDateTime startTime) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
        this.taskType = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String nameTask, String descriptionTask, Status status, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
        this.taskType = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
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

    // Новый метод для получения времени завершения задачи
    public LocalDateTime getEndTime() {
        return startTime != null && duration != null ? startTime.plus(duration) : null;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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
        return nameTask + " (Start: " + startTime + ", Duration: " + duration + ")";
    }
}




