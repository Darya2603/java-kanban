package tracker.manager.impl;

import tracker.manager.HistoryManager;
import tracker.manager.exception.ManagerSaveException;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.status.Status;
import tracker.status.TaskType;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.HashSet;


public class FileBackedTaskManager extends InMemoryTaskManagerImpl {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            throw new ManagerSaveException("Директория не существует: " + parentDir.getAbsolutePath(), null);
        }
        if (!file.exists()) {
            try {
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
            writer.write("id,type,name,status,description,duration,startTime,epic\n");
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
                + task.getDescriptionTask() + "," + task.getDuration() + "," + task.getStartTime() + "," + epicId;
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) {
        if (!file.exists()) {
            throw new ManagerSaveException("Файл не существует: " + file.getAbsolutePath(), null);
        }

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Set<Integer> existingIds = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                Task task = fromString(line);
                int taskId = task.getId();

                if (!existingIds.add(taskId)) {
                    throw new IllegalArgumentException("Задача с таким идентификатором уже существует: " + taskId);
                }

                if (task instanceof Epic epic) {
                    manager.createEpic(epic);
                } else if (task instanceof Subtask subtask) {
                    manager.createSubtask(subtask);
                } else {
                    manager.createTask(task);
                }
            }
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла", e);
        }

        return manager;
    }


    private static Task fromString(String line) {
        String[] fields = line.split(",");
        if (fields.length < 8) {
            throw new IllegalArgumentException("Недостаточно данных для создания задачи");
        }
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        Duration duration = Duration.parse(fields[5]);
        LocalDateTime startTime = LocalDateTime.parse(fields[6]);
        int epicId = Integer.parseInt(fields[7]);

        return switch (type) {
            case TASK -> new Task(id, name, description, status, type, duration, startTime);
            case EPIC -> new Epic(id, name, description, status, duration, startTime);
            case SUBTASK -> new Subtask(id, name, description, status, duration, startTime, epicId);
        };
    }
}







