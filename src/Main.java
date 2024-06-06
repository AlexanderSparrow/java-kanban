import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.HistoryService;
import service.Services;
import service.TaskService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        //TaskService taskService = new TaskService(); // Создание менеджера задач
        TaskService taskService = Services.getDefault(); // Создание менеджера задач

        System.out.println("Добавляем задачи...");// добавление задач
        taskService.addTask(new Task(0, "first task",
                "write code of final task of 4 sprint", Status.IN_PROGRESS));
        taskService.addTask(new Task(1, "second task",
                "check the logic of the program", Status.IN_PROGRESS));
        taskService.addTask(new Task(2, "third task",
                "submit the third task of the 4th sprint for review", Status.IN_PROGRESS));

        taskService.addEpic(new Epic(124, "epic1", "first epic"));
        taskService.addEpic(new Epic(155, "epic2", "second epic"));

        taskService.addSubTask(new SubTask(444, "first subtask",
                "write code of final task of 4 sprint", Status.DONE, getEpicByName("epic1", taskService)));
        taskService.addSubTask(new SubTask(555, "second subtask",
                "check the code for functionality", Status.IN_PROGRESS, getEpicByName("epic1", taskService)));

        taskService.addSubTask(new SubTask(2, "first subtask2",
                "relax", Status.DONE, getEpicByName("epic2", taskService)));
        taskService.addSubTask(new SubTask(3, "second subtask2",
                "continue relax", Status.IN_PROGRESS, getEpicByName("epic2", taskService)));
        taskService.addSubTask(new SubTask(5, "third subtask2",
                "continue relax more", Status.NEW, getEpicByName("epic2", taskService)));

        listOfTasks(taskService);

        System.out.println("Обновили задачу:");
        taskService.updateTask(new Task(1, "updatedName", "UpdatedDescription", Status.NEW));
        listOfTasks(taskService);

        System.out.println("\nУдаляем задачу...");
        taskService.removeTask(1);
        listOfTasks(taskService);

        System.out.println("Статус эпика: " + getEpicByName("epic1", taskService).getStatus());
        listOfSubTasksByEpicById(4, taskService);
        System.out.println("Статус эпика: " + getEpicByName("epic2", taskService).getStatus());
        listOfSubTasksByEpic("epic2", taskService);

        System.out.println("\nЗадачи по ID:");
        System.out.println(taskService.getTaskById(2));
        System.out.println(taskService.getTaskById(3));
        System.out.println(taskService.getEpicById(4));
        System.out.println(taskService.getSubTaskById(5));
        System.out.println(taskService.getSubTaskById(6));
        System.out.println(taskService.getSubTaskById(7));
        System.out.println(taskService.getSubTaskById(8));
        System.out.println(taskService.getSubTaskById(9));
        System.out.println(taskService.getSubTaskById(10));


        System.out.println("\nСписок задач:");
        listOfTasks(taskService);

        System.out.println("\nСписок подзадач:");
        listOfSubTasks(taskService);

        System.out.println("\nСписок Эпиков:");
        listOfEpics(taskService);

        System.out.println("\nУдаляем подзадачу...");
        System.out.println("Удалили подзадачу.");
        taskService.removeSubTask(9);
        listOfSubTasks(taskService);
        listOfEpics(taskService);
        listOfSubTasksByEpic("epic1", taskService);
        listOfSubTasksByEpic("epic2", taskService);


        System.out.println("\nОбновили подзадачу:");
        taskService.updateSubTask(new SubTask(6, "updatedName", "UpdatedDescription",
                Status.DONE, getEpicByName("epic1", taskService)));
        taskService.updateSubTask(new SubTask(8, "updatedName2", "UpdatedDescription2",
                Status.NEW, taskService.getEpicById(5)));
        System.out.println(getEpicByName("epic1", taskService).getStatus());
        listOfSubTasksByEpic("epic1", taskService);
        System.out.println(getEpicByName("epic2", taskService).getStatus());
        listOfSubTasksByEpic("epic2", taskService);


        System.out.println("\nОбновили эпик:");
        taskService.updateEpic(new Epic(4, "updated epic name", "updated epic description"));
        listOfEpics(taskService);
        listOfSubTasksByEpic(taskService.getEpicById(4).getName(), taskService);


        System.out.println("\nУдаляем эпик...");
        System.out.println("Удалили эпик.");
        taskService.removeEpic(4);
        listOfSubTasks(taskService);
        listOfEpics(taskService);


        System.out.println("Удаляем все задачи...");
        taskService.removeAllTasks();
        listOfSubTasks(taskService);
        System.out.println(" ");

        removeAllSubTasks(taskService);
        taskService.removeAllEpics();
        System.out.println("История просмотра задач:");
        System.out.println(Arrays.toString(taskService.historyService.getTaskHistoryList()));
        System.out.println("Программа завершена");
    }

    private static void removeAllSubTasks(TaskService taskService) {
        listOfSubTasks (taskService);
        System.out.println("\nУдаляем все подзадачи...");
        taskService.removeAllSubTasks();
        listOfSubTasks (taskService);
    }

    private static void listOfTasks(TaskService taskService) {
        List <Task> tasks = taskService.getTasks();
        if (!tasks.isEmpty()) {
            System.out.println("Список задач (списком):");
            for (Task task : taskService.getTasks()) {
                System.out.println("ID: " + task.getId() + ", Название: " + task.getName() + ", Описание: "
                        + task.getDescription() + ", Статус: " + task.getStatus());
            }
        } else {
            System.out.println("Список задач пуст.");
        }
        System.out.println();
    }

    public static void listOfSubTasks(TaskService taskService) {
        List<SubTask> tasks = taskService.getSubTasks();
        if (!tasks.isEmpty()) {
            System.out.println("Список подзадач (списком):");
            for (Task task : tasks) {
                System.out.println("ID: " + task.getId() + ", Название: " + task.getName() + ", Описание: "
                        + task.getDescription() + ", Статус: " + task.getStatus());
            }
        } else {
            System.out.println("Список подзадач пуст.");
        }
        System.out.println();
    }

    public static void listOfEpics(TaskService taskService) {
        List<Epic> tasks = taskService.getEpics();
        if (!tasks.isEmpty()) {
            System.out.println("Список эпиков (списком):");
            for (Task task : tasks) {
                System.out.println("ID: " + task.getId() + ", Название: " + task.getName() + ", Описание: "
                        + task.getDescription() + ", Статус: " + task.getStatus());
            }
        } else {
            System.out.println("Список 'эпиков' пуст.");
        }
        System.out.println();
    }

    private static void listOfSubTasksByEpicById(int id, TaskService taskService) {
        System.out.println("Список подзадач " + taskService.getEpicById(id).getName() + " (списком):");
        Set<SubTask> subTasks = taskService.getAllSubTasksByEpic(id);
        for (SubTask subTask : subTasks) {
            System.out.println("ID: " + subTask.getId() + ", Название: " + subTask.getName() + ", Описание: "
                    + subTask.getDescription() + ", Статус: " + subTask.getStatus());
        }
        System.out.println();
    }

    private static void listOfSubTasksByEpic(String epicName, TaskService taskService) {
        Set<SubTask> subTasks = taskService.getAllSubTasksByEpic(getEpicByName(epicName, taskService).getId());
        for (SubTask subTask : subTasks) {
            System.out.println("ID: " + subTask.getId() + ", Название: " + subTask.getName() + ", Описание: "
                    + subTask.getDescription() + ", Статус: " + subTask.getStatus());
        }
        System.out.println();
    }

    public static Epic getEpicByName(String name, TaskService taskService) {
        Epic foundedEpic = null;
        for (Epic epic : taskService.getEpics()) {
            if (epic.getName().equals(name)) {
                foundedEpic = epic;
                break;
            }
        }
        return foundedEpic;
    }

}
