package tracker.model;

import tracker.manager.impl.InMemoryTaskManagerImpl;
import tracker.manager.impl.InMemoryHistoryManagerImpl;
import tracker.status.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи должны быть равны по id.");
    }

    // Тестирование неизменности задачи при добавлении
    @Test
    void testTaskImmutabilityOnAdd() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        int taskId = manager.createTask(task);
        Task retrievedTask = manager.getTaskById(taskId);
        assertEquals(task, retrievedTask);
        assertEquals(task.getNameTask(), retrievedTask.getNameTask());
        assertEquals(task.getDescriptionTask(), retrievedTask.getDescriptionTask());
        assertEquals(task.getStatus(), retrievedTask.getStatus());
    }
}



