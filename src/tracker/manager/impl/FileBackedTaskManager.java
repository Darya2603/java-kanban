package tracker.manager.impl;

import tracker.manager.HistoryManager;
import tracker.manager.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.status.Status;
import tracker.status.TaskType;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManagerImpl implements TaskManager {
    private static File file1;
    private final File file;


    public FileBackedTaskManager(File file) {
        this.file = file;
        if (!file.exists()) {
            try {
                File parentDir = file.getParentFile();
                if (parentDir != null) {
                    parentDir.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка при создании файла", e);
            }
        }
    }

    public static File getFile1() {
        return file1;
    }

    @Override
    public int createTask(Task task) {
        if (getTasks().stream().anyMatch(t -> t.getId() == task.getId())) {
            throw new IllegalArgumentException("Задача с таким идентификатором уже существует");
        }
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
        if (getEpics().stream().anyMatch(e -> e.getId() == epic.getId())) {
            throw new IllegalArgumentException("Эпик с таким идентификатором уже существует");
        }
        int epicId = super.createEpic(epic);
        save();
        return epicId;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        if (getSubtasks().stream().anyMatch(s -> s.getId() == subtask.getId())) {
            throw new IllegalArgumentException("Подзадача с таким идентификатором уже существует");
        }
        int subtaskId = super.createSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    public static String toString(Task task) {
        int epicId = 0;
        if (task instanceof Subtask subtask) {
            epicId = subtask.getEpicId();
        }
        return task.getId() + "," + task.getTaskType() + "," + task.getNameTask() + "," + task.getStatus() + ","
                + task.getDescriptionTask() + "," + epicId;
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) {

        if (!file.exists()) {
            throw new ManagerSaveException("Файл не существует: " + file.getAbsolutePath(), null);
        }
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() > 1) {
                for (String line : lines.subList(1, lines.size())) {
                    if (line.trim().isEmpty()) continue;
                    Task task = fromString(line);
                    if (task instanceof Epic epic) {
                        manager.createEpic(epic);
                    } else if (task instanceof Subtask subtask) {
                        manager.createSubtask(subtask);
                        Epic epic = manager.getEpicById((subtask.getEpicId()));
                        if (epic != null) {
                            epic.addSubtask(subtask);
                        } else {
                            throw new IllegalArgumentException("Эпик с ID " + subtask.getEpicId() + " не найден.");
                        }
                    } else {
                        manager.createTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        if (fields.length < 5) {
            throw new IllegalArgumentException("Недостаточно данных");
        }
        final int id = Integer.parseInt(fields[0]);
        final TaskType type = TaskType.valueOf(fields[1]);
        final String name = fields[2];
        final Status status = Status.valueOf(fields[3]);
        final String description = fields[4];
        final int epicId = fields.length > 5 ? Integer.parseInt(fields[5]) : 0;

        return switch (type) {
            case TASK -> new Task(id, name, description, status);
            case EPIC -> new Epic(id, name, description);
            case SUBTASK -> new Subtask(id, name, description, status, epicId);
        };
    }
    public static void main(String[] args) {
        File file = new File("resources/task.csv");
        FileBackedTaskManager manager = getFileBackedTaskManager(file);


        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println("Loaded Tasks:");
        assert loadedManager != null;
        for (Task task : loadedManager.getTasks()) {
            System.out.println(task);
        }
        for (Epic epic : loadedManager.getEpics()) {
            System.out.println(epic);
        }
        for (Subtask subtask : loadedManager.getSubtasks()) {
            System.out.println(subtask);
        }
    }

    private static FileBackedTaskManager getFileBackedTaskManager(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task1 = new Task("Task #1", "Description for Task #1", Status.NEW);
        Task task2 = new Task("Task #2", "Description for Task #2", Status.IN_PROGRESS);
        Epic epic1 = new Epic("Epic #1", "Description for Epic #1");
        Subtask subtask1 = new Subtask("Subtask #1", "Description for Subtask #1", Status.NEW, epic1.getId());
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        return manager;
    }

    private static FileBackedTaskManager loadFromFile(File file) {
        file1 = file;
        return null;
    }
}






