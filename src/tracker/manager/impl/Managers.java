package tracker.manager.impl;


import tracker.manager.HistoryManager;
import tracker.manager.TaskManager;

public class Managers {

    private Managers() {
    }

    public static TaskManager getInMemoryTaskManager(HistoryManager historyManager) {
        return new InMemoryTaskManagerImpl(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManagerImpl();

    }
}

