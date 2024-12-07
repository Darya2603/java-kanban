package tracker.manager.impl;

import tracker.manager.HistoryManager;
import tracker.manager.TaskManager;

import java.io.File;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("resources/task.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManagerImpl();
    }
}


