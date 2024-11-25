package tracker.manager;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.List;

public interface TaskManager {
    // Для добавления задач
    int addNewTask(Task task);

    int addNewSubtask(Subtask subtask);

    int addNewEpic(Epic epic);

    // Обновление задач
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    // Получение задач по id
    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    // Удаление задач
    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    // Удаление всех задач определенного типа
    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();


    // Получение всех задач
    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    List<Task> getHistory();

    List<Subtask> getEpicSubtasks(int epicId);

    void removeTask(int taskId);

    void removeSubtask(int subtaskId);
}