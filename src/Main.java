import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.FileBackedTaskService;
import service.InMemoryTaskService;
import service.Services;

import java.io.File;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        //InMemoryTaskService inMemoryTaskService = Services.getDefault(); // Создание менеджера задач
        File file = new File("./data/tasks.csv");
        FileBackedTaskService inMemoryTaskService = FileBackedTaskService.loadFromFile(file);// Создание менеджера задач
        //FileBackedTaskService.loadFromFile(file);
        System.out.println("Добавляем задачи...");// добавление задач
        inMemoryTaskService.addTask(new Task(0, "first task",
                "write code of final task of 4 sprint", Status.IN_PROGRESS));

        inMemoryTaskService.addEpic(new Epic(124, "epic1", "first epic"));

        inMemoryTaskService.addSubTask(new SubTask(444, "first subtask",
                "write code of final task of 4 sprint", Status.DONE, getEpicByName("epic1",
                inMemoryTaskService)));

        inMemoryTaskService.addSubTask(new SubTask(2, "first subtask2",
                "relax", Status.DONE, getEpicByName("epic2", inMemoryTaskService)));

    }

    private static void removeAllSubTasks(InMemoryTaskService inMemoryTaskService) {
        listOfSubTasks(inMemoryTaskService);
        System.out.println("\nУдаляем все подзадачи...");
        inMemoryTaskService.removeAllSubTasks();
        listOfSubTasks(inMemoryTaskService);
    }

    private static void listOfTasks(InMemoryTaskService inMemoryTaskService) {
        List<Task> tasks = inMemoryTaskService.getTasks();
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
            for (SubTask subTask : tasks) { // Исправлено на SubTask
                System.out.println("ID: " + subTask.getId() + ", Название: " + subTask.getName() + ", Описание: "
                        + subTask.getDescription() + ", Статус: " + subTask.getStatus());
            }
        } else {
            System.out.println("Список подзадач пуст.");
        }
        System.out.println();
    }

    public static void listOfEpics(InMemoryTaskService inMemoryTaskService) {
        List<Epic> epics = inMemoryTaskService.getEpics();
        if (!epics.isEmpty()) {
            System.out.println("Список эпиков (списком):");
            for (Epic epic : epics) { // Исправлено на Epic
                System.out.println("ID: " + epic.getId() + ", Название: " + epic.getName() + ", Описание: "
                        + epic.getDescription() + ", Статус: " + epic.getStatus());
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
