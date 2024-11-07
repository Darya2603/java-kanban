package model;

import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.Status;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        task2.setId(1);
        assertEquals(task1, task2);
    }

    @Test
    void testSubtaskEqualityById() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, 1);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, 1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2);
    }

    @Test
    void testEpicSubtaskSelfAddition() {
        Epic epic = new Epic("Epic 1", "Description 1");
        assertThrows(IllegalArgumentException.class, () -> epic.addSubtaskId(epic.getId()));
    }


    @Test
    void testSubtaskSelfEpic() {
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, 1);
        assertThrows(IllegalArgumentException.class, () -> subtask.setEpicId(subtask.getId()));
    }

    @Test
    void testManagersInitialization() {
        assertNotNull(Managers.getDefaultTaskManager());
        assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void testInMemoryTaskManagerAddAndFind() {
        InMemoryTaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
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
        InMemoryTaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        int taskId1 = manager.addNewTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        task2.setId(taskId1); // Try to set the same ID
        int taskId2 = manager.addNewTask(task2); // Should not throw an exception
        assertNotEquals(taskId1, taskId2);
    }

    @Test
    void testTaskImmutabilityOnAdd() {
        InMemoryTaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        int taskId = manager.addNewTask(task);
        Task retrievedTask = manager.getTask(taskId);
        assertEquals(task, retrievedTask);
        assertEquals(task.getName(), retrievedTask.getName());
        assertEquals(task.getDescription(), retrievedTask.getDescription());
        assertEquals(task.getStatus(), retrievedTask.getStatus());

    }

    @Test
    void testHistoryManagerPreservesPreviousTaskVersion() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.getFirst());

        task.setStatus(Status.DONE);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertNotEquals(history.get(0).getStatus(), history.get(1).getStatus());

    }
}
