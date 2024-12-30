package tracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.manager.TaskManager;
import tracker.manager.impl.InMemoryTaskManagerImpl;
import tracker.status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManagerImpl();
    }

    // Тестирование добавления подзадач к эпикам
    @Test
    void testAddSubtask() {
        Epic epic = new Epic(1, "Epic 1", "Description 1", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.now());
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.now(), epic.getId());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(21), epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic.getId());
        assertEquals(2, subtasks.size(), "Epic should have 2 subtasks added.");
        assertTrue(subtasks.contains(subtask1), "Epic should contain subtask 1.");
        assertTrue(subtasks.contains(subtask2), "Epic should contain subtask 2.");
    }

    // Тестирование удаления подзадачи по ID
    @Test
    void testRemoveSubtaskId() {
        Epic epic = new Epic(1, "Epic 1", "Description 1", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.now());
        taskManager.createEpic(epic);

        // Создание подзадач без явного указания id
        Subtask subtask1 = new Subtask(0, "Subtask 1", "Description 1", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.now(), epic.getId());
        Subtask subtask2 = new Subtask(0, "Subtask 2", "Description 2", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.now().plusMinutes(21), epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.removeSubtaskById(subtask1.getId());

        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic.getId());

        assertEquals(1, subtasks.size(), "Epic should have 1 subtask remaining.");
        assertFalse(subtasks.stream().anyMatch(subtask -> subtask.getId() == subtask1.getId()),
                "Epic should not contain subtask 1 after removal.");
        assertTrue(subtasks.stream().anyMatch(subtask -> subtask.getId() == subtask2.getId()),
                "Epic should still contain subtask 2.");
    }

    // Тестирование очистки подзадач у эпиков
    @Test
    void testCleanSubtasks() {
        Epic epic = new Epic(1, "Epic 1", "Description 1", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.now());
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.now(), epic.getId());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.now().plusMinutes(21), epic.getId());

        System.out.println("Subtasks before cleaning: " + epic.getSubtasks().size());
        epic.cleanSubtasks();
        System.out.println("Subtasks after cleaning: " + epic.getSubtasks().size());

        assertTrue(epic.getSubtasks().isEmpty(), "Epic should have no subtasks after cleaning.");
    }

    // Тестирование статуса эпика с подзадачами
    @Test
    public void testEpicStatus() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, Duration.ofHours(1),
                LocalDateTime.now());
        taskManager.createEpic(epic);

        LocalDateTime now = LocalDateTime.now();
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description",
                Status.IN_PROGRESS, Duration.ofHours(1), now, epic.getId());
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description",
                Status.DONE, Duration.ofHours(1), now.plusHours(1), epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(),
                "Epic status should be IN_PROGRESS.");
    }

    // Тестирование статуса эпика, когда все подзадачи завершены
    @Test
    public void testEpicStatusAllDone() {
        Epic epic = new Epic(0, "Epic 1", "Epic description", Status.NEW, Duration.ZERO,
                LocalDateTime.now());
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(0, "Subtask 1", "Description 1", Status.DONE,
                Duration.ofMinutes(20), LocalDateTime.now(), epic.getId());
        Subtask subtask2 = new Subtask(1, "Subtask 2", "Description 2", Status.DONE,
                Duration.ofMinutes(25), LocalDateTime.now().plusMinutes(30), epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        assertEquals(Status.DONE, epic.getStatus());
    }

    // Тестирование статуса эпика с подзадачами со статусами NEW и DONE
    @Test
    public void testEpicStatusNewAndDone() {
        Epic epic = new Epic(0, "Epic 1", "Epic description", Status.NEW, Duration.ZERO,
                LocalDateTime.now());
        taskManager.createEpic(epic);

        LocalDateTime startTime = LocalDateTime.now();

        Subtask subtask1 = new Subtask(0, "Subtask 1", "Description 1", Status.NEW,
                Duration.ofMinutes(20), startTime, epic.getId());
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask(0, "Subtask 2", "Description 2", Status.IN_PROGRESS,
                Duration.ofMinutes(25), startTime.plusMinutes(20), epic.getId());
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    // Тестирование статуса эпика с подзадачами со статусом IN_PROGRESS
    @Test
    public void testEpicStatusInProgress() {
        Epic epic = new Epic(0, "Epic 1", "Epic description", Status.NEW, Duration.ZERO,
                LocalDateTime.now());
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(0, "Subtask 1", "Description 1", Status.IN_PROGRESS,
                Duration.ofMinutes(20), LocalDateTime.now(), epic.getId());
        taskManager.createSubtask(subtask1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}