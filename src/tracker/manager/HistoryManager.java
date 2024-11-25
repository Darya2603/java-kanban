package tracker.manager;

import tracker.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    default void remove(int id) {
    }
    List<Task> getHistory();
}


