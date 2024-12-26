package tracker.manager;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskManager {

    int createTask(Task task);

    Integer createSubtask(Subtask subtask);

    int createEpic(Epic epic);

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    Optional<Task> getTaskById(int id);

    Optional<Subtask> getSubtaskById(int id);

    Optional<Epic> getEpicById(int id);

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}