package tracker.manager.impl;

import tracker.manager.exception.ManagerSaveException;
import tracker.model.Task;
import tracker.status.Status;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = File.createTempFile("tempTasks", ".csv");
        tempFile.deleteOnExit();
        manager = new FileBackedTaskManager(tempFile);
    }

    // Тестирование сохранения и загрузки пустого файла
    @Test
    public void testSaveAndLoadEmptyFile() {
        Assertions.assertEquals(0, tempFile.length());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Assertions.assertTrue(loadedManager.getTasks().isEmpty());
        Assertions.assertTrue(loadedManager.getEpics().isEmpty());
        Assertions.assertTrue(loadedManager.getSubtasks().isEmpty());
    }

    // Тестирование сохранения нескольких задач
    @Test
    public void testSaveMultipleTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS, Duration.ofMinutes(45), LocalDateTime.now());
        manager.createTask(task1);
        manager.createTask(task2);

        Assertions.assertEquals(2, manager.getTasks().size());

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Assertions.assertEquals(2, loadedManager.getTasks().size());
        Assertions.assertEquals(task1.getNameTask(), loadedManager.getTasks().get(0).getNameTask());
        Assertions.assertEquals(task2.getNameTask(), loadedManager.getTasks().get(1).getNameTask());
    }

    @Test
    public void testLoadMultipleTasksWithDuplicateIds() throws IOException {
        String content = """
            id,type,name,status,description,epic
            1,TASK,Task 1,NEW,Description 1,0
            1,TASK,Task 2,IN_PROGRESS,Description 2,0
            """;

        Files.writeString(tempFile.toPath(), content);

        assertThrows(IllegalArgumentException.class, () -> FileBackedTaskManager.loadFromFile(tempFile),
                "Задача с таким идентификатором уже существует: 1");
    }

    // Тестирование обработки ошибок
    @Test
    public void testLoadFromNonExistentFile() {
        File nonExistentFile = new File("test/resources/non_existent_file.csv");

        if (nonExistentFile.exists()) {
            Assertions.assertTrue(nonExistentFile.delete());
        }

        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile));
    }

    // Тестирование обработки пустых строк
    @Test
    public void testLoadWithEmptyLines() throws IOException {
        String content = """
                id,type,name,status,description,epic
                1,TASK,Task 1,NEW,Description 1,0
                
                2,TASK,Task 2,IN_PROGRESS,Description 2,0
                """;
        Files.writeString(tempFile.toPath(), content);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Assertions.assertEquals(2, loadedManager.getTasks().size());
        Assertions.assertEquals("Task 1", loadedManager.getTasks().get(0).getNameTask());
        Assertions.assertEquals("Task 2", loadedManager.getTasks().get(1).getNameTask());
    }

    @Test
    void getFile1() {
    }
}
