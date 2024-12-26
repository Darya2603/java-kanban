package tracker.model;

import tracker.status.Status;
import tracker.status.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime; // дата завершения эпика

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, null);
        this.taskType = TaskType.EPIC;
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW, null, null);
        this.taskType = TaskType.EPIC;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(int subtaskId) {
        subtasks.removeIf(subtask -> subtask.getId() == subtaskId);
    }

    public void cleanSubtasks() {
        subtasks.clear();
    }

    private void updateEpicDetails() {
        if (subtasks.isEmpty()) {
            duration = Duration.ZERO;
            startTime = null;
            endTime = null;
        } else {
            duration = subtasks.stream()
                    .map(Subtask::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);
            startTime = subtasks.stream()
                    .map(Subtask::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            endTime = subtasks.stream()
                    .map(Subtask::getEndTime)
                    .filter(ignored -> endTime != null)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
        }
    }

    @Override
    public void setNameTask(String nameTask) {
        super.setNameTask(nameTask);
    }

    public Status getStatus() {
        return super.getStatus();
    }

    public void setStatus(Status status) {
        super.setStatus(status);
    }

    @Override
    public void setEpicId(int epicId) {
        super.setEpicId(epicId);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        for (Subtask subtask : subtasks) {
            if (subtask.getEpicId() == this.getId()) {
                throw new IllegalArgumentException("Эпик не может добавлять себя же в качестве подзадачи.");
            }
        }
        this.subtasks = subtasks;
        updateEpicDetails();
    }

    @Override
    public Duration getDuration() {
        return subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getStartTime() {
        return subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public String toString() {
        return nameTask + ": " + subtasks + " (Start: " + startTime + ", Duration: " + duration + ")";
    }
}









