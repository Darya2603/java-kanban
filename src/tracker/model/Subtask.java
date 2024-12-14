package tracker.model;

import tracker.status.Status;
import tracker.status.TaskType;

public class Subtask extends Task {
    private static int idCounter = 0;
    private int epicId;
    private int id;

    public Subtask(String nameTask, String descriptionTask,Status status, int epicId) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
        this.epicId = epicId;
        this.id = ++idCounter;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(int id, String nameTask, String descriptionTask, Status status, int epicId) {
        super(id, nameTask, descriptionTask, status);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}







