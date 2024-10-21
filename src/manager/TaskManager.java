package manager;

import model.Epic;
import model.Status;
import model.Subtask;

import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    public Map<Integer, Subtask> subtasks = new HashMap<>();
     public Map<Integer, Epic> epics = new HashMap<>();

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        // Обновление статуса Epic
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    public void updateEpicStatus(Epic epic) {
        boolean hasSubtasks = false;
        boolean allDone = true;

        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                hasSubtasks = true;
                if (subtask.getStatus() != Status.DONE) {
                    allDone = false;
                }
            }
        }

        if (!hasSubtasks || allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void printSubtasks() {
        System.out.println("Subtasks: " + subtasks.values());
    }

    public void printEpics() {
        System.out.println("Epics: " + epics.values());
    }
}




