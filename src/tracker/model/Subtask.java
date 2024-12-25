package tracker.model;

import tracker.status.Status;
import tracker.status.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private static int idCounter = 0;
    private int epicId;

    public Subtask(String nameTask, String descriptionTask, Status status, int epicId, Duration duration, LocalDateTime startTime ) {
        super(nameTask, descriptionTask, status, duration, startTime);
        this.epicId = epicId;
        this.id = ++idCounter;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(int id, String nameTask, String descriptionTask, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        super(id, nameTask, descriptionTask, status, duration, startTime);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public void setDuration(Duration duration) {
        super.setDuration(duration);
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        super.setStartTime(startTime);
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}







