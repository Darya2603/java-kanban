package tracker.model;

import tracker.manager.impl.InMemoryTaskManagerImpl;
import tracker.manager.impl.InMemoryHistoryManagerImpl;
import tracker.status.Status;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskTest {

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofMinutes(45), LocalDateTime.now());

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи должны быть равны по id.");
    }

    @Test
    void testTaskImmutabilityOnAdd() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        int taskId = manager.createTask(task);

        Optional<Task> retrievedTask = manager.getTaskById(taskId);

        // Проверяем, что retrievedTask содержит значение
        assertTrue(retrievedTask.isPresent(), "Task should be present in the manager");

        // Сравниваем объекты
        assertEquals(task, retrievedTask.get(), "The retrieved task should be equal to the original task");
        assertEquals(task.getNameTask(), retrievedTask.get().getNameTask(), "Task names should match");
        assertEquals(task.getDescriptionTask(), retrievedTask.get().getDescriptionTask(), "Task descriptions should match");
        assertEquals(task.getStatus(), retrievedTask.get().getStatus(), "Task statuses should match");
    }
}


