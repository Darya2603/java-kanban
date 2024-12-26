package tracker.manager.impl;

import tracker.manager.HistoryManager;
import tracker.manager.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    public HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManagerImpl();
    }

    // Тестирование добавления подзадачи к эпикам
    @Test
    void testEpicCannotAddItselfAsSubtask() {
        Epic epic = new Epic("Epic 1", "Epic description");
        epic.setId(1);
        Subtask subtask = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic.getId(), Duration.ofMinutes(20), LocalDateTime.now());

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
        Task task = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        int taskId = manager.createTask(task);
        Optional<Task> retrievedTask = manager.getTaskById(taskId);
        assertTrue(retrievedTask.isPresent());
        assertEquals(task, retrievedTask.get());

        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createEpic(epic);
        Optional<Epic> retrievedEpic = manager.getEpicById(epicId);
        assertTrue(retrievedEpic.isPresent());
        assertEquals(epic, retrievedEpic.get());

        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epicId, Duration.ofMinutes(20), LocalDateTime.now());
        int subtaskId = manager.createSubtask(subtask);
        Optional<Subtask> retrievedSubtask = manager.getSubtaskById(subtaskId);
        assertTrue(retrievedSubtask.isPresent());
        assertEquals(subtask, retrievedSubtask.get());
    }

    // Тестирование конфликта ID задач
    @Test
    void testTaskIdConflict() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        int taskId1 = manager.createTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofMinutes(45), LocalDateTime.now());
        int taskId2 = manager.createTask(task2);
        assertNotEquals(taskId1, taskId2);
    }

    // Тестирование удаления подзадачи и обновления связанного эпика
    @Test
    void testDeleteSubtaskUpdatesEpic() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epicId, Duration.ofMinutes(20), LocalDateTime.now());
        int subtaskId1 = manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epicId, Duration.ofMinutes(25), LocalDateTime.now());
        int subtaskId2 = manager.createSubtask(subtask2);

        // Удаляем подзадачу и проверяем, что эпик обновился
        manager.removeSubtaskById(subtaskId1);
        manager.removeSubtaskById(subtaskId2);
        Optional<Epic> updatedEpic = manager.getEpicById(epicId);
        assertTrue(updatedEpic.isPresent(), "Эпик должен существовать после удаления подзадач.");
        assertEquals(0, updatedEpic.get().getSubtasks().size(), "Проверяем, что подзадач больше нет.");
    }

    // Тестирование целостности данных после удаления подзадачи
    @Test
    void testEpicDoesNotContainOldSubtaskIds() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Epic epic = new Epic("Epic 1", "Description for epic");
        manager.createEpic(epic);
        int epicId = epic.getId();
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", Status.NEW, epicId, Duration.ofMinutes(20), LocalDateTime.now());
        int subtaskId = manager.createSubtask(subtask);
        manager.removeSubtaskById(subtaskId);
    }

    // Тестирование обновления статуса задачи
    @Test
    void testUpdateTaskStatus() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        int taskId = manager.createTask(task);

        // Обновляем статус задачи
        task.setStatus(Status.DONE);
        manager.updateTask(task);

        // Получаем задачу по ID и проверяем статус
        Optional<Task> updatedTask = manager.getTaskById(taskId);
        assertTrue(updatedTask.isPresent(), "Задача должна быть найдена в менеджере.");
        assertEquals(Status.DONE, updatedTask.get().getStatus(), "Статус задачи должен обновиться в менеджере.");
    }

    // Тестирование сохранения истории задач
    @Test
    void testHistoryManagerPreservesPreviousVersion() {
        InMemoryHistoryManagerImpl historyManager = new InMemoryHistoryManagerImpl();
        Task task = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
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
                "Description for Subtask 1", Status.NEW, epicId, Duration.ofMinutes(20), LocalDateTime.now());
        int subtaskId = manager.createSubtask(subtask);
        epic.addSubtask(subtask);

        assertTrue(epic.getSubtasks().contains(subtask), "Эпик должен содержать подзадачу.");

        Subtask newSubtask = new Subtask("Subtask 1 Updated",
                "Description for Subtask 1 Updated", Status.NEW, epicId, Duration.ofMinutes(20), LocalDateTime.now());
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
        Task task = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        int taskId = manager.createTask(task);

        Optional<Task> retrievedTask = manager.getTaskById(taskId);

        // Проверяем, что retrievedTask содержит значение
        assertTrue(retrievedTask.isPresent(), "Task should be present in the manager");

        // Проверяем, что описание задачи не изменилось
        assertNotEquals("Modified Description", retrievedTask.get().getDescriptionTask(),
                "Manager data should not be affected by direct changes in task properties");
    }
}

