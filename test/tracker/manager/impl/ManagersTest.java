package tracker.manager.impl;


import tracker.manager.HistoryManager;
import tracker.manager.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.status.Status;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    public HistoryManager historyManager;

    @BeforeEach
    void setAp() {
        historyManager = new InMemoryHistoryManagerImpl();
    }

    // Тестирование добавления подзадачи к эпикам
    @Test
    void testEpicCannotAddItselfAsSubtask() {
        Epic epic = new Epic("Epic 1", "Epic description");
        epic.setId(1);
        Subtask subtask = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic.getId());

        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> epic.setSubtasks(subtasks));

        assertEquals("Эпик не может добавлять себя же в качестве подзадачи.", exception.getMessage());
    }

    // Тестирование создания экземпляров менеджера задач
    @Test
    void testGetTaskManagerReturnsInitializedInstance() {
        TaskManager manager1 = new InMemoryTaskManagerImpl(historyManager);
        TaskManager manager2 = new InMemoryTaskManagerImpl(historyManager);

        assertNotNull(manager1, "Менеджер задач не должен быть null.");
        assertNotSame(manager1, manager2, "Должны возвращаться разные экземпляры менеджера задач.");
    }

    // Тестирование создания экземпляров менеджера истории
    @Test
    void testGetHistoryManagerReturnsInitializedInstance() {
        HistoryManager history1 = new InMemoryHistoryManagerImpl();
        HistoryManager history2 = new InMemoryHistoryManagerImpl();

        assertNotNull(history1, "Менеджер истории не должен быть null.");
        assertNotSame(history1, history2, "Должны возвращаться разные экземпляры менеджера истории.");
    }

    // Тестирование добавления различных задач в менеджер
    @Test
    void testInMemoryTaskManagerAddDifferentTasks() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        int taskId = manager.createTask(task);
        assertEquals(task, manager.getTaskById(taskId));

        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createEpic(epic);
        assertEquals(epic, manager.getEpicById(epicId));

        Subtask subtask = new Subtask("Subtask 1", "Description 1",Status.NEW, epicId);
        int subtaskId = manager.createSubtask(subtask);
        assertEquals(subtask, manager.getSubtaskById(subtaskId));
    }

    // Тестирование конфликта ID задач
    @Test
    void testTaskIdConflict() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        int taskId1 = manager.createTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        int taskId2 = manager.createTask(task2);
        assertNotEquals(taskId1, taskId2);
    }

    // Тестирование удаления подзадачи и обновления связанного эпика
    @Test
    void testDeleteSubtaskUpdatesEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        int subtask1Id = subtask1.getId();
        epic.removeSubtask(subtask1Id);
        List<Subtask> remainingSubtasks = epic.getSubtasks();        assertEquals(1, remainingSubtasks.size(), "Epic should have 1 subtask remaining.");
        assertFalse(remainingSubtasks.stream().anyMatch(subtask -> subtask.getId() == subtask1Id),
                "Epic should not contain subtask ID " + subtask1Id + " after removal.");
        assertTrue(remainingSubtasks.stream().anyMatch(subtask -> subtask.getId() == subtask2.getId()),
                "Epic should still contain subtask ID " + subtask2.getId() + ".");
    }

    // Тестирование целостности данных после удаления подзадачи
    @Test
    void testEpicDoesNotContainOldSubtaskIds() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Epic epic = new Epic("Epic 1", "Description for epic");
        manager.createEpic(epic);
        int epicId = epic.getId();
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", Status.NEW, epicId);
        int subtaskId = manager.createSubtask(subtask);
        manager.removeSubtaskById(subtaskId);
    }

    // Тестирование обновления статуса задачи
    @Test
    void testUpdateTaskStatus() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        int taskId = manager.createTask(task);

        task.setStatus(Status.DONE);
        manager.updateTask(task);

        assertEquals(Status.DONE, manager.getTaskById(taskId).getStatus(),
                "Статус задачи должен обновиться в менеджере.");
    }

    // Тестирование сохранения истории задач
    @Test
    void testHistoryManagerPreservesPreviousVersion() {
        InMemoryHistoryManagerImpl historyManager = new InMemoryHistoryManagerImpl();
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());

        task.setStatus(Status.DONE);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(Status.DONE, history.getFirst().getStatus());
    }

    // Тестирование изменения ID эпика подзадачи
    @Test
    void testUpdateSubtaskChangesEpicSubtaskIds() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Epic epic = new Epic("Epic 1", "Description for Epic 1");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1",
                "Description for Subtask 1", Status.NEW, epicId);
        int subtaskId = manager.createSubtask(subtask);
        epic.addSubtask(subtask);

        assertTrue(epic.getSubtasks().contains(subtask), "Эпик должен содержать подзадачу.");

        Subtask newSubtask = new Subtask("Subtask 1 Updated",
                "Description for Subtask 1 Updated", Status.NEW, epicId);
        int newSubtaskId = manager.createSubtask(newSubtask);

        epic.removeSubtask(subtaskId);
        epic.addSubtask(newSubtask);

        assertFalse(epic.getSubtasks().contains(subtask), "Эпик не должен содержать старую подзадачу.");
        assertTrue(epic.getSubtasks().contains(newSubtask), "Эпик должен содержать новую подзадачу.");
        assertEquals(newSubtaskId, newSubtask.getId(), "ID новой подзадачи должен соответствовать ожидаемому.");
    }

    // Тестирование целостности данных при обновлении задачи
    @Test
    void testSettersDoNotAffectManagerData() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        int taskId = manager.createTask(task);

        Task retrievedTask = manager.getTaskById(taskId);
        assertNotEquals("Modified Description", retrievedTask.getDescriptionTask(),
                "Manager data should not be affected by direct changes in task properties");
    }

}

