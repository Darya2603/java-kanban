package tracker.manager.impl;

import tracker.manager.HistoryManager;
import tracker.manager.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.status.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Optional;

public class InMemoryTaskManagerImpl implements TaskManager {

    private static int countID = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager;


    public InMemoryTaskManagerImpl(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public InMemoryTaskManagerImpl() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int createTask(Task task) {
        if (task.getId() != 0 && tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Задачи с одинаковым id не должны добавляться.");
        }
        countID++;
        task.setId(countID);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Epic with ID " + subtask.getEpicId() + " does not exist.");
        }
        subtask.setId(++countID);
        subtasks.put(subtask.getId(), subtask);
        epic.getSubtasks().add(subtask);
        updateEpicStatus(epic.getId());
        return subtask.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        countID++;
        epic.setId(countID);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void removeTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Task task : epics.values()) {
            historyManager.remove(task.getId());
        }
        epics.clear();
        for (Task task : subtasks.values()) {
            historyManager.remove(task.getId());
        }
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic.getId());
        }
        for (Task task : subtasks.values()) {
            historyManager.remove(task.getId());
        }
        subtasks.clear();
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return Optional.of(task);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            return Optional.of(subtask);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return Optional.of(epic);
        }
        return Optional.empty();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setDescriptionTask(epic.getDescriptionTask());
        updateEpicStatus(existingEpic.getId());
        epics.put(epic.getId(), existingEpic);
    }

    @Override
    public void removeTaskById(int id) {
        Optional<Task> task = getTaskById(id);
        if (task.isPresent()) {

            historyManager.remove(id);
            tasks.remove(task.get().getId());
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Optional<Epic> epic = getEpicById(subtask.getEpicId());
            if (epic.isPresent()) {
                epic.get().removeSubtask(subtask.getId());
                updateEpicStatus(epic.get().getId());
            }
            subtasks.remove(id);
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            }
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {

        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        // Используем TreeSet для хранения задач по времени начала
        TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subtasks.values());
        return new ArrayList<>(prioritizedTasks);
    }

    private void updateEpicStatus(int epicId) {
        Optional<Epic> epic = getEpicById(epicId);
        if (epic.isPresent()) {
            List<Subtask> subtasks = getEpicSubtasks(epicId);
            boolean allDone = true;
            boolean anyInProgress = false;

            for (Subtask subtask : subtasks) {
                if (subtask.getStatus() == Status.NEW) {
                    allDone = false;
                    break;
                }
                if (subtask.getStatus() == Status.IN_PROGRESS) {
                    anyInProgress = true;
                }
            }
            if (allDone) {
                epic.get().setStatus(Status.DONE);
            } else if (anyInProgress) {
                epic.get().setStatus(Status.IN_PROGRESS);
            } else {
                epic.get().setStatus(Status.NEW);
            }
        }
    }
}
