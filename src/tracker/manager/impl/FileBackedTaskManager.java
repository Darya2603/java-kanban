package tracker.manager.impl;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.status.Status;
import tracker.status.TaskType;
import tracker.manager.exception.ManagerSaveException;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;


public class FileBackedTaskManager extends InMemoryTaskManagerImpl {
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
            writer.write("id,type,name,status,description,epic,duration,startTime\n");
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

        // Проверка на null для duration и startTime
        String durationString = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "0";
        String startTimeString = task.getStartTime() != null ? task.getStartTime().toString() : "null";

        return task.getId() + "," + task.getTaskType() + "," + task.getNameTask() + "," + task.getStatus() + ","
                + task.getDescriptionTask() + "," + epicId + "," + durationString + "," + startTimeString;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (!file.exists()) {
            throw new ManagerSaveException("Файл не существует: " + file.getAbsolutePath(), null);
        }

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Set<Integer> taskIds = new HashSet<>(); // Для отслеживания уникальных идентификаторов задач

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() > 1) {
                for (String line : lines.subList(1, lines.size())) {
                    if (line.trim().isEmpty()) continue; // Пропускаем пустые строки

                    Task task = fromString(line);
                    if (taskIds.contains(task.getId())) {
                        throw new IllegalArgumentException("Задача с ID " + task.getId() + " уже существует.");
                    }
                    taskIds.add(task.getId()); // Добавляем ID задачи в множество

                    if (task instanceof Epic epic) {
                        manager.createEpic(epic);
                    } else if (task instanceof Subtask subtask) {
                        manager.createSubtask(subtask);
                        Optional<Epic> epic = manager.getEpicById(subtask.getEpicId());
                        if (epic.isPresent()) {
                            epic.get().addSubtask(subtask);
                        } else {
                            throw new IllegalArgumentException("Эпик с ID " + subtask.getEpicId() + " не найден.");
                        }
                    } else {
                        manager.createTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла", e);
        }
        return manager;
    }

    // Обновленный метод fromString
    public static Task fromString(String line) {
        String[] fields = line.split(",");
        final int id = Integer.parseInt(fields[0]);
        final TaskType type = TaskType.valueOf(fields[1]);
        final String name = fields[2];
        final Status status = Status.valueOf(fields[3]);
        final String description = fields[4];
        final int epicId = fields.length > 5 ? Integer.parseInt(fields[5]) : 0;
        Duration duration = fields.length > 6 ? Duration.ofMinutes(Long.parseLong(fields[6])) : null;
        LocalDateTime startTime = fields.length > 7 ? LocalDateTime.parse(fields[7]) : null;

        return switch (type) {
            case TASK -> new Task(id, name, description, status, duration, startTime);
            case EPIC -> new Epic(id, name, description);
            case SUBTASK -> new Subtask(id, name, description, status, epicId, duration, startTime);
        };
    }

    public static void main(String[] args) {
        File file = new File("resources/task.csv");
        getFileBackedTaskManager(file);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println("Loaded Tasks:");
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

    private static void getFileBackedTaskManager(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task1 = new Task("Task #1", "Description for Task #1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task #2", "Description for Task #2", Status.IN_PROGRESS, Duration.ofMinutes(45), LocalDateTime.now());
        Epic epic1 = new Epic("Epic #1", "Description for Epic #1");
        Subtask subtask1 = new Subtask("Subtask #1", "Description for Subtask #1", Status.NEW, epic1.getId(), Duration.ofMinutes(20), LocalDateTime.now());
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
    }
}





