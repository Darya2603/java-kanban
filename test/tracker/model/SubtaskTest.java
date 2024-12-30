package tracker.model;

import tracker.status.Status;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    // Тестирование равенства подзадач по ID
    @Test
    void testSubtaskEqualityById() {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", Status.NEW,
                Duration.ofHours(1), LocalDateTime.now(), 1);
        Subtask subtask2 = new Subtask(1, "Subtask 2", "Description 2", Status.NEW,
                Duration.ofHours(1), LocalDateTime.now(), 1);

        assertEquals(subtask1, subtask2, "Subtasks should be equal by id.");
    }
}
