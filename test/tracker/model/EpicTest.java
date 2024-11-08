package tracker.model;

import org.junit.jupiter.api.Test;
import tracker.status.Status;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

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
}

