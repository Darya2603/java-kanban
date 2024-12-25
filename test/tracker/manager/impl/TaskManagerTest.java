package tracker.manager.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.manager.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.status.Status;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    public void setUp() {
        manager = createManager();
    }

    @Test
    public void testCreateTask() {
        Task task = new Task("Task #1", "Task1 description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        int taskId = manager.createTask(task);
        assertEquals(1, manager.getTasks().size());
        assertEquals(taskId, task.getId());
    }

    @Test
    public void testCreateSubtaskWithEpic() {
        Epic epic = new Epic("Epic 1", "Epic description");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId(),Duration.ofMinutes(20), LocalDateTime.now());
        int subtaskId = manager.createSubtask(subtask1);
        assertEquals(1, manager.getSubtasks().size());
        assertEquals(subtaskId, subtask1.getId());
        assertEquals(1, manager.getEpicSubtasks(epic.getId()).size());
    }

    @Test
    public void testEpicStatusCalculation() {
        Epic epic = new Epic("Epic 1", "Epic description");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, 1, Duration.ofMinutes(20), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, 1, Duration.ofMinutes(25), LocalDateTime.now());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testRemoveTask() {
        Task task = new Task("Task #1", "Task1 description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);
        manager.removeTaskById(task.getId());
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    public void testRemoveEpic() {
        Epic epic = new Epic("Epic 1", "Epic description");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId(),Duration.ofMinutes(20), LocalDateTime.now());
        manager.createSubtask(subtask1);
        manager.removeEpicById(epic.getId());
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    // Добавьте тест на корректность расчёта пересечения интервалов
    @Test
    public void testTaskOverlap() {
        Task task1 = new Task("Task #1", "Task1 description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        task1.setStartTime(LocalDateTime.of(2023, 10, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(30));
        manager.createTask(task1);

        Task task2 = new Task("Task #1", "Task1 description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        task2.setStartTime(LocalDateTime.of(2023, 10, 1, 10, 15));
        task2.setDuration(Duration.ofMinutes(30));

        assertThrows(IllegalArgumentException.class, () -> manager.createTask(task2), "Задачи пересекаются по времени.");
    }
}

