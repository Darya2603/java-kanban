package tracker.model;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    void testAddSubtaskId() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic.addSubtaskId(1); // Добавляем ID подзадачи
        epic.addSubtaskId(2); // Добавляем еще один ID подзадачи

        List<Integer> subtaskIds = epic.getSubtaskIds();
        assertEquals(2, subtaskIds.size(), "Epic should have 2 subtasks added.");
        assertTrue(subtaskIds.contains(1), "Epic should contain subtask ID 1.");
        assertTrue(subtaskIds.contains(2), "Epic should contain subtask ID 2.");
    }

    @Test
    void testRemoveSubtaskId() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic.addSubtaskId(1);
        epic.addSubtaskId(2);

        epic.removeSubtask(1); // Удаляем ID подзадачи

        List<Integer> subtaskIds = epic.getSubtaskIds();
        assertEquals(1, subtaskIds.size(), "Epic should have 1 subtask remaining.");
        assertFalse(subtaskIds.contains(1), "Epic should not contain subtask ID 1 after removal.");
        assertTrue(subtaskIds.contains(2), "Epic should still contain subtask ID 2.");
    }

    @Test
    void testCleanSubtaskIds() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic.addSubtaskId(1);
        epic.addSubtaskId(2);

        epic.cleanSubtaskIds(); // Очищаем список подзадач

        List<Integer> subtaskIds = epic.getSubtaskIds();
        assertTrue(subtaskIds.isEmpty(), "Epic should have no subtasks after cleaning.");
    }
}
