import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.InMemoryTaskService;
import service.Services;

import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskService inMemoryTaskService = Services.getDefault(); // Создание менеджера задач
        System.out.println("Добавляем задачи...");// добавление задач
        inMemoryTaskService.addTask(new Task(0, "first task",
                "write code of final task of 4 sprint", Status.IN_PROGRESS));
        inMemoryTaskService.addTask(new Task(1, "second task",
                "check the logic of the program", Status.IN_PROGRESS));
        inMemoryTaskService.addTask(new Task(2, "third task",
                "submit the third task of the 4th sprint for review", Status.IN_PROGRESS));

        inMemoryTaskService.addEpic(new Epic(124, "epic1", "first epic"));
        inMemoryTaskService.addEpic(new Epic(155, "epic2", "second epic"));

        inMemoryTaskService.addSubTask(new SubTask(444, "first subtask",
                "write code of final task of 4 sprint", Status.DONE, getEpicByName("epic1", inMemoryTaskService)));
        inMemoryTaskService.addSubTask(new SubTask(555, "second subtask",
                "check the code for functionality", Status.IN_PROGRESS, getEpicByName("epic1", inMemoryTaskService)));

        inMemoryTaskService.addSubTask(new SubTask(2, "first subtask2",
                "relax", Status.DONE, getEpicByName("epic2", inMemoryTaskService)));
        inMemoryTaskService.addSubTask(new SubTask(3, "second subtask2",
                "continue relax", Status.IN_PROGRESS, getEpicByName("epic2", inMemoryTaskService)));
        inMemoryTaskService.addSubTask(new SubTask(5, "third subtask2",
                "continue relax more", Status.NEW, getEpicByName("epic2", inMemoryTaskService)));

        listOfTasks(inMemoryTaskService);

        System.out.println("Обновили задачу:");
        inMemoryTaskService.updateTask(new Task(1, "updatedName", "UpdatedDescription", Status.NEW));
        listOfTasks(inMemoryTaskService);

        System.out.println("\nУдаляем задачу...");
        inMemoryTaskService.removeTask(1);
        listOfTasks(inMemoryTaskService);

        System.out.println("Статус эпика: " + getEpicByName("epic1", inMemoryTaskService).getStatus());
        listOfSubTasksByEpicById(4, inMemoryTaskService);
        System.out.println("Статус эпика: " + getEpicByName("epic2", inMemoryTaskService).getStatus());
        listOfSubTasksByEpic("epic2", inMemoryTaskService);

        System.out.println("\nЗадачи по ID:");
        System.out.println(inMemoryTaskService.getTaskById(2));
        System.out.println(inMemoryTaskService.getTaskById(3));
        System.out.println(inMemoryTaskService.getEpicById(4));
        System.out.println(inMemoryTaskService.getSubTaskById(5));
        System.out.println(inMemoryTaskService.getSubTaskById(6));
        System.out.println(inMemoryTaskService.getSubTaskById(7));
        System.out.println(inMemoryTaskService.getSubTaskById(8));
        System.out.println(inMemoryTaskService.getSubTaskById(9));
        System.out.println(inMemoryTaskService.getSubTaskById(10));


        System.out.println("\nСписок задач:");
        listOfTasks(inMemoryTaskService);

        System.out.println("\nСписок подзадач:");
        listOfSubTasks(inMemoryTaskService);

        System.out.println("\nСписок Эпиков:");
        listOfEpics(inMemoryTaskService);

        System.out.println("\nУдаляем подзадачу...");
        System.out.println("Удалили подзадачу.");
        inMemoryTaskService.removeSubTask(9);
        listOfSubTasks(inMemoryTaskService);
        listOfEpics(inMemoryTaskService);
        listOfSubTasksByEpic("epic1", inMemoryTaskService);
        listOfSubTasksByEpic("epic2", inMemoryTaskService);


        System.out.println("\nОбновили подзадачу:");
        inMemoryTaskService.updateSubTask(new SubTask(6, "updatedName", "UpdatedDescription",
                Status.DONE, getEpicByName("epic1", inMemoryTaskService)));
        inMemoryTaskService.updateSubTask(new SubTask(8, "updatedName2", "UpdatedDescription2",
                Status.NEW, inMemoryTaskService.getEpicById(5)));
        System.out.println(getEpicByName("epic1", inMemoryTaskService).getStatus());
        listOfSubTasksByEpic("epic1", inMemoryTaskService);
        System.out.println(getEpicByName("epic2", inMemoryTaskService).getStatus());
        listOfSubTasksByEpic("epic2", inMemoryTaskService);


        System.out.println("\nОбновили эпик:");
        inMemoryTaskService.updateEpic(new Epic(4, "updated epic name", "updated epic description"));
        listOfEpics(inMemoryTaskService);
        listOfSubTasksByEpic(inMemoryTaskService.getEpicById(4).getName(), inMemoryTaskService);


        System.out.println("\nУдаляем эпик...");
        System.out.println("Удалили эпик.");
        inMemoryTaskService.removeEpic(4);
        listOfSubTasks(inMemoryTaskService);
        listOfEpics(inMemoryTaskService);


        System.out.println("Удаляем все задачи...");
        inMemoryTaskService.removeAllTasks();
        listOfSubTasks(inMemoryTaskService);
        System.out.println(" ");

        removeAllSubTasks(inMemoryTaskService);
        inMemoryTaskService.removeAllEpics();
        System.out.println("История просмотра задач:");
        System.out.println(inMemoryTaskService.getHistory());
        System.out.println("Программа завершена");
    }

    private static void removeAllSubTasks(InMemoryTaskService inMemoryTaskService) {
        listOfSubTasks (inMemoryTaskService);
        System.out.println("\nУдаляем все подзадачи...");
        inMemoryTaskService.removeAllSubTasks();
        listOfSubTasks (inMemoryTaskService);
    }

    private static void listOfTasks(InMemoryTaskService inMemoryTaskService) {
        List <Task> tasks = inMemoryTaskService.getTasks();
        if (!tasks.isEmpty()) {
            System.out.println("Список задач (списком):");
            for (Task task : tasks) {
                System.out.println("ID: " + task.getId() + ", Название: " + task.getName() + ", Описание: "
                        + task.getDescription() + ", Статус: " + task.getStatus());
            }
        } else {
            System.out.println("Список задач пуст.");
        }
        System.out.println();
    }

    public static void listOfSubTasks(InMemoryTaskService inMemoryTaskService) {
        List<SubTask> tasks = inMemoryTaskService.getSubTasks();
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

    public static void listOfEpics(InMemoryTaskService inMemoryTaskService) {
        List<Epic> tasks = inMemoryTaskService.getEpics();
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

    private static void listOfSubTasksByEpicById(int id, InMemoryTaskService inMemoryTaskService) {
        System.out.println("Список подзадач " + inMemoryTaskService.getEpicById(id).getName() + " (списком):");
        Set<SubTask> subTasks = inMemoryTaskService.getAllSubTasksByEpic(id);
        for (SubTask subTask : subTasks) {
            System.out.println("ID: " + subTask.getId() + ", Название: " + subTask.getName() + ", Описание: "
                    + subTask.getDescription() + ", Статус: " + subTask.getStatus());
        }
        System.out.println();
    }

    private static void listOfSubTasksByEpic(String epicName, InMemoryTaskService inMemoryTaskService) {
        Set<SubTask> subTasks = inMemoryTaskService.getAllSubTasksByEpic(getEpicByName(epicName, inMemoryTaskService).getId());
        for (SubTask subTask : subTasks) {
            System.out.println("ID: " + subTask.getId() + ", Название: " + subTask.getName() + ", Описание: "
                    + subTask.getDescription() + ", Статус: " + subTask.getStatus());
        }
        System.out.println();
    }

    public static Epic getEpicByName(String name, InMemoryTaskService inMemoryTaskService) {
        List<Epic> tasks = inMemoryTaskService.getEpics();
        Epic foundedEpic = null;
        for (Epic epic : tasks) {
            if (epic.getName().equals(name)) {
                foundedEpic = epic;
                break;
            }
        }
        return foundedEpic;
    }
}
