package tracker.model;

import org.junit.jupiter.api.Test;
import tracker.manager.impl.InMemoryHistoryManagerImpl;
import tracker.manager.impl.InMemoryTaskManagerImpl;
import tracker.status.Status;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        task2.setId(1);
        assertEquals(task1, task2);
    }

    @Test
    void testTaskImmutabilityOnAdd() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        int taskId = manager.addNewTask(task);
        Task retrievedTask = manager.getTask(taskId);
        assertEquals(task, retrievedTask);
        assertEquals(task.getName(), retrievedTask.getName());
        assertEquals(task.getDescription(), retrievedTask.getDescription());
        assertEquals(task.getStatus(), retrievedTask.getStatus());
    }

}



