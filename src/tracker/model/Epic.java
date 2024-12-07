package tracker.model;

import tracker.status.Status;
import tracker.status.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.taskType = TaskType.EPIC;
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
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
    }

    @Override
    public String toString() {
        return nameTask + ": " +
                subtasks;
    }
}








