import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskService;

public class Main {

    public static void main(String[] args) {
        TaskService taskService = new TaskService(); // Создание менеджера задач

        System.out.println("Добавляем задачи...");// добавление задач
        Task task1 = new Task(taskService.getNextCounter(), "first task",
                "write code of final task of 4 sprint", Status.IN_PROGRESS);
        taskService.addTask(task1);
        taskService.createTask("second task", "check the logic of the program", Status.IN_PROGRESS);
        taskService.createTask("third task", "submit the third task of the 4th sprint for review");

        Epic epic1 = new Epic(taskService.getNextCounter(), "epic1", "first epic");
        taskService.addEpic(epic1);
        taskService.createEpic("epic2", "second epic");

        SubTask subTask1 = new SubTask(taskService.getNextCounter(), "first subtask",
                "write code of final task of 4 sprint", Status.IN_PROGRESS, epic1);
        taskService.addSubTask(subTask1);
        taskService.createSubTask("second subtask", "check the code for functionality", Status.DONE, epic1);
        taskService.createSubTask("first subtask2", "relax", Status.IN_PROGRESS, "epic2");
        taskService.createSubTask("second subtask2", "continue relax", 5);
        taskService.createSubTask("third subtask2", "continue relax more", Status.NEW, 5);

        listOfTasks(taskService);

        System.out.println("Обновили задачу:");
        Task updatedTask1 = new Task(1, "updatedName", "UpdatedDescription", Status.NEW);
        taskService.updateTask(updatedTask1);
        taskService.printAllTasks();


        System.out.println("\nУдаляем задачу...");
        taskService.removeTask(1);
        listOfTasks(taskService);

        System.out.println("Статус эпика: " + epic1.getStatus());
        listOfSubTasksByEpic(epic1, taskService);

        System.out.println("Статус эпика: " + taskService.getEpicByName("epic2").getStatus());
        listOfSubTasksByEpic("epic2", taskService);

        System.out.println("\nЗадачи по ID:");
        System.out.println(taskService.getTaskById(2));
        System.out.println(taskService.geSubTaskById(9));
        System.out.println(taskService.getEpicById(5));

        System.out.println("\nСписок задач:");
        taskService.printAllTasks();

        System.out.println("\nСписок подзадач:");
        taskService.printAllSubTasks();

        System.out.println("\nСписок Эпиков:");
        taskService.printAllEpics();

        System.out.println("\nУдаляем подзадачу...");
        System.out.println("Удалили подзадачу.");
        taskService.removeSubTask(9);
        taskService.printAllSubTasks();
        taskService.printAllEpics();

        System.out.println("\nОбновили подзадачу:");
        SubTask updatedSubTask1 = new SubTask(6, "updatedName", "UpdatedDescription",
                Status.DONE, epic1);
        System.out.println(taskService.getEpicByName("epic1").getStatus());
        taskService.updateSubTask(updatedSubTask1);
        SubTask updatedSubTask2 = new SubTask(8, "updatedName2", "UpdatedDescription2", Status.NEW, taskService.getEpicById(5));
        taskService.updateSubTask(updatedSubTask2);

        listOfSubTasksByEpic(epic1, taskService);
        listOfSubTasksByEpic("epic2", taskService);

        System.out.println("\nУдаляем эпик...");
        System.out.println("Удалили эпик.");
        taskService.removeEpic(4);
        taskService.printAllSubTasks();
        taskService.printAllEpics();


        System.out.println("Удаляем все задачи...");
        taskService.removeAllTasks();
        taskService.printAllTasks();
        System.out.println(" ");

        removeAllSubTasks(taskService);
        taskService.removeAllEpics();
        System.out.println("Программа завершена");
    }

    private static void removeAllSubTasks(TaskService taskService) {
        taskService.printAllSubTasks();
        System.out.println("\nУдаляем все подзадачи...");
        taskService.removeAllSubTasks();
        taskService.printAllSubTasks();
    }

    private static void listOfTasks(TaskService taskService) {

        taskService.printAllTasks();
        System.out.println();
    }

    private static void listOfSubTasksByEpic(Epic epic, TaskService taskService) {

        System.out.println("Список подзадач " + epic.getName() + " (списком):");
        taskService.printAllSubTasksByEpic(epic);
        System.out.println();
    }

    private static void listOfSubTasksByEpic(String epicName, TaskService taskService) {
        System.out.println("Список подзадач " + epicName + " (списком):");
        taskService.printAllSubTasksByEpic(taskService.getEpicByName(epicName));
        System.out.println();
    }
}
