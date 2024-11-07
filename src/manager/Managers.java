package manager;


public class Managers {

    private Managers() {
    }

    public static TaskManager getInMemoryTaskManager(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Object getDefaultTaskManager() {
        return null;
    }
}

