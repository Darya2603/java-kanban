import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;

public class Main {
        public static void main(String[] args) {
                TaskManager manager = new TaskManager();

                // Создание задач
                Epic epic1 = new Epic("Epic 1", "Description 1");
                Epic epic2 = new Epic("Epic 2", "Description 2");

                Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic1.getId());
                Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic1.getId());
                Subtask subtask3 = new Subtask("Subtask 3", "Description 3", epic2.getId());
                // Добавление задач в менеджер
                manager.addEpic(epic1);
                manager.addEpic(epic2);
                manager.addSubtask(subtask1);
                manager.addSubtask(subtask2);
                manager.addSubtask(subtask3);

                // Вывод текущих задач
                manager.printEpics();
                manager.printSubtasks();

                // Изменение статуса подзадачи
                subtask1.setStatus(Status.IN_PROGRESS);
                manager.updateSubtask(subtask1);

                // Вывод обновленного статуса
                System.out.println("Updated status of epic 1: " + epic1);

                // Удаление подзадачи и вывод статуса эпика
                manager.subtasks.remove(subtask1.getId()); // Удаляем подзадачу
                System.out.println("After deleting subtask 1 status of epic 1: " + epic1);
        }
}
