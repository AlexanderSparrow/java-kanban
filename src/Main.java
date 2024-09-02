import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.FileBackedTaskService;
import service.InMemoryTaskService;
import service.HttpTaskServer;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        File file = new File("./data/tasks.csv");
        FileBackedTaskService fileBackedTaskService = FileBackedTaskService.loadFromFile(file);// Создание менеджера задач
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Duration duration = Duration.ofMinutes(4);

        HttpTaskServer server = new HttpTaskServer(fileBackedTaskService);
        server.start();

        System.out.println("Prioritized Tasks:");
        for (Task task : fileBackedTaskService.getPrioritizedTasks()) {
            System.out.println(task.toString());
        }

        System.out.println("Добавляем задачи...");// добавление задач
        fileBackedTaskService.addTask(new Task(0, "first task",
                "write code of final task of 4 sprint", Status.IN_PROGRESS, duration, LocalDateTime.now().plusHours(1)));

        fileBackedTaskService.addEpic(new Epic(124, "epic1", "first epic"));

        fileBackedTaskService.addSubTask(new SubTask(444, "first subtask",
                "write code of final task of 4 sprint", Status.DONE, duration, LocalDateTime.now(),
                getEpicByName("epic1",
                        fileBackedTaskService)));
        fileBackedTaskService.addSubTask(new SubTask(444, "second subtask",
                "test code of final task of 4 sprint", Status.NEW, duration, LocalDateTime.now().plusMinutes(5),
                getEpicByName("epic1",
                        fileBackedTaskService)));
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
            tasks.forEach(task -> System.out.println("ID: " + task.getId() + ", Название: " + task.getName()
                    + ", Описание: " + task.getDescription() + ", Статус: " + task.getStatus()));
        } else {
            System.out.println("Список задач пуст.");
        }
        System.out.println();
    }

    public static void listOfSubTasks(InMemoryTaskService inMemoryTaskService) {
        List<SubTask> tasks = inMemoryTaskService.getSubTasks();
        if (!tasks.isEmpty()) {
            System.out.println("Список подзадач (списком):");
            tasks.forEach(subTask -> System.out.println("ID: " + subTask.getId() + ", Название: " + subTask.getName()
                    + ", Описание: " + subTask.getDescription() + ", Статус: " + subTask.getStatus()));
        } else {
            System.out.println("Список подзадач пуст.");
        }
        System.out.println();
    }

    public static void listOfEpics(InMemoryTaskService inMemoryTaskService) {
        List<Epic> epics = inMemoryTaskService.getEpics();
        if (!epics.isEmpty()) {
            System.out.println("Список эпиков (списком):");
            epics.forEach(epic -> System.out.println("ID: " + epic.getId() + ", Название: " + epic.getName()
                    + ", Описание: " + epic.getDescription() + ", Статус: " + epic.getStatus()));
        } else {
            System.out.println("Список 'эпиков' пуст.");
        }
        System.out.println();
    }

    private static void listOfSubTasksByEpicById(int id, InMemoryTaskService inMemoryTaskService) {
        Epic epic = inMemoryTaskService.getEpicById(id);
        System.out.println("Список подзадач " + epic.getName() + " (списком):");
        inMemoryTaskService.getAllSubTasksByEpic(id).forEach(subTask -> System.out.println("ID: " + subTask.getId()
                + ", Название: " + subTask.getName() + ", Описание: " + subTask.getDescription()
                + ", Статус: " + subTask.getStatus()));
        System.out.println();
    }

    private static void listOfSubTasksByEpic(String epicName, InMemoryTaskService inMemoryTaskService) {
        Epic epic = getEpicByName(epicName, inMemoryTaskService);
        inMemoryTaskService.getAllSubTasksByEpic(epic.getId()).forEach(subTask -> System.out.println("ID: "
                + subTask.getId() + ", Название: " + subTask.getName() + ", Описание: " + subTask.getDescription()
                + ", Статус: " + subTask.getStatus()));
        System.out.println();
    }

    public static Epic getEpicByName(String name, InMemoryTaskService inMemoryTaskService) {
        return inMemoryTaskService.getEpics().stream()
                .filter(epic -> epic.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

}
