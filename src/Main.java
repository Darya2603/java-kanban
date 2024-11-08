import tracker.manager.impl.Managers;
import tracker.manager.TaskManager;
import tracker.model.Epic;
import tracker.status.Status;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {
        public static void main(String[] args) {
                TaskManager manager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());



                // Создание задач
                Task task1 = new Task("Task #1", "Task1 description", Status.NEW);
                Task task2 = new Task("Task #2", "Task2 description", Status.IN_PROGRESS);
                final int taskId1 = manager.addNewTask(task1);
                final int taskId2 = manager.addNewTask(task2);

                Epic epic1 = new Epic("Epic #1", "Epic1 description");
                Epic epic2 = new Epic("Epic #2", "Epic2 description");
                final int epicId1 = manager.addNewEpic(epic1);
                final int epicId2 = manager.addNewEpic(epic2);

                Subtask subtask1 = new Subtask("Subtask #1-1", "Subtask1 description", Status.NEW, epicId1);
                Subtask subtask2 = new Subtask("Subtask #2-1", "Subtask1 description", Status.NEW, epicId1);
                Subtask subtask3 = new Subtask("Subtask #3-2", "Subtask1 description", Status.DONE, epicId2);
                manager.addNewSubtask(subtask1);
                final int subtaskId2 = manager.addNewSubtask(subtask2);
                final int subtaskId3 = manager.addNewSubtask(subtask3);


                printAllTasks(manager);

                // Обновление
                final Task task = manager.getTask(taskId2);
                task.setStatus(Status.DONE);
                manager.updateTask(task);
                System.out.println("CHANGE STATUS: Task2 IN_PROGRESS->DONE");

                printAllTasks(manager);

                Subtask subtask = manager.getSubtask(subtaskId2);
                subtask.setStatus(Status.DONE);
                manager.updateSubtask(subtask);
                System.out.println("CHANGE STATUS: Subtask2 NEW->DONE");
                subtask = manager.getSubtask(subtaskId3);
                subtask.setStatus(Status.NEW);
                manager.updateSubtask(subtask);
                System.out.println("CHANGE STATUS: Subtask3 DONE->NEW");

                printAllTasks(manager);

                // Удаление
                System.out.println("DELETE: Task1");
                manager.deleteTask(taskId1);
                System.out.println("DELETE: Epic1");
                manager.deleteEpic(epicId1);
                manager.deleteSubtask(subtaskId2);
                manager.deleteSubtasks();
                manager.deleteEpics();
                manager.deleteTasks();


                printAllTasks(manager);
        }

        public static void printAllTasks(TaskManager manager) {
                System.out.println("Задачи:");
                for (Task task : manager.getTasks()) {
                        System.out.println(task);
                }

                System.out.println("Эпики:");
                for (Epic epic : manager.getEpics()) {
                        System.out.println(epic);
                        for (Subtask subtask : manager.getEpicSubtasks(epic.getId())) {
                                System.out.println("--> " + subtask);
                        }
                }

                System.out.println("Подзадачи:");
                for (Subtask subtask : manager.getSubtasks()) {
                        System.out.println(subtask);
                }

                 System.out.println("История:");
                for (Task task : manager.getHistory()) {
                System.out.println(task);
                }
       }
    }
