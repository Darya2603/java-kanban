package tracker.model;

import tracker.manager.impl.InMemoryTaskManagerImpl;
import tracker.manager.impl.InMemoryHistoryManagerImpl;
import tracker.status.Status;
import org.junit.jupiter.api.Test;
import tracker.status.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    // Тестирование равенства задач по ID
    @Test
    void testTaskEqualityById() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, TaskType.TASK,
                Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, TaskType.TASK,
                Duration.ofHours(1), LocalDateTime.now());

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи должны быть равны по id.");
    }

    // Тестирование неизменности задачи при добавлении
    @Test
    void testTaskImmutabilityOnAdd() {
        InMemoryTaskManagerImpl manager = new InMemoryTaskManagerImpl(new InMemoryHistoryManagerImpl());
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW, TaskType.TASK,
                Duration.ofHours(1), LocalDateTime.now());
        int taskId = manager.createTask(task);
        Task retrievedTask = manager.getTaskById(taskId);
        assertEquals(task, retrievedTask);
        assertEquals(task.getNameTask(), retrievedTask.getNameTask());
        assertEquals(task.getDescriptionTask(), retrievedTask.getDescriptionTask());
        assertEquals(task.getStatus(), retrievedTask.getStatus());
    }
}


