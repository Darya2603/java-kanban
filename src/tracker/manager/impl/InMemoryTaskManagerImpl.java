package tracker.manager.impl;

import tracker.manager.HistoryManager;
import tracker.manager.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.status.Status;

import java.util.* ;
public class InMemoryTaskManagerImpl implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager;
    private int idCounter = 0;

    public InMemoryTaskManagerImpl(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }




    @Override
    public int addNewSubtask(Subtask subtask) {
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        }
        return subtask.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    // Обновление задач
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // Получение задач по id
    @Override
    public Task getTask(int id) {
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        return epics.get(id);
    }

    // Удаление задач
    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    // Удаление всех задач определенного типа
    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    // Получение всех задач
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
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
    @Override
    public int addNewTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        historyManager.add(task); // Add to history
        return task.getId();
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = getEpic(epicId);
        List<Subtask> result = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            result.add(subtasks.get(subtaskId));
        }
        return result;
    }

    @Override
    public void removeTask(int taskId) {

    }

    @Override
    public void removeSubtask(int subtaskId) {

    }

    public void updateEpicStatus(Epic epic) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        if (epicSubtasks.isEmpty() || epicSubtasks.stream().allMatch(st -> st.getStatus() == Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else if (epicSubtasks.stream().allMatch(st -> st.getStatus() == Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        updateEpic(epic);
    }
}
