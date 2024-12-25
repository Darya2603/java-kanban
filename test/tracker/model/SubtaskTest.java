package tracker.model;

import tracker.status.Status;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    @Test
    void testSubtaskEqualityById() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, 1, Duration.ofMinutes(20), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, 1, Duration.ofMinutes(25), LocalDateTime.now());

        subtask1.setId(1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Подзадачи должны быть равны по id.");
    }
}
