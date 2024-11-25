package tracker.manager.impl;

import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.status.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    void testHistoryManagerPreservesPreviousTaskVersion() {
        InMemoryHistoryManagerImpl historyManager = new InMemoryHistoryManagerImpl();
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());


        task.setStatus(Status.DONE);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(1, history.size()); // Убедимся, что в истории только последний просмотр

    }

    @Test
    void testInMemoryTaskManagerAddAndFind() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        int taskId = manager.addNewTask(task);
        assertEquals(task, manager.getTask(taskId));

        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, 1);
        int subtaskId = manager.addNewSubtask(subtask);
        assertEquals(subtask, manager.getSubtask(subtaskId));

        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.addNewEpic(epic);
        assertEquals(epic, manager.getEpic(epicId));
    }

    @Test
    void testTaskIdsDoNotConflict() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        int taskId1 = manager.addNewTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        // task2.setId(taskId1); // Не нужно устанавливать id вручную, так как он генерируется менеджером.
        int taskId2 = manager.addNewTask(task2);
        assertNotEquals(taskId1, taskId2);
    }

    @Test
    void testRemoveTaskUpdatesHistory() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        int taskId = manager.addNewTask(task);
        manager.removeTask(taskId); // Удаляем задачу
        manager.getHistory();// Получаем историю
    }

    @Test
    void testEpicDoesNotContainOldSubtaskIds() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Epic epic = new Epic("Epic 1", "Description for epic");
        manager.addNewEpic(epic);
        int epicId = epic.getId(); // Получаем ID эпика
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", Status.NEW, epicId);
        int subtaskId = manager.addNewSubtask(subtask);
        manager.removeSubtask(subtaskId);
    }

    @Test
    void testSettersDoNotAffectManagerData() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        int taskId = manager.addNewTask(task);

        // Обновляем свойства задачи
        task.setDescription("Modified Description");

        // Получаем задачу из менеджера
        Task retrievedTask = manager.getTask(taskId);
        assertNotEquals("Modified Description", retrievedTask.getDescription(), "Manager data should not be affected by direct changes in task properties");
    }
}


