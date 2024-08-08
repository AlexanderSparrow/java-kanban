import model.*;
import service.FileBackedTaskService;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileBackedTaskManagerTest {
    public static void main(String[] args) {
        try {
            File tempFile = File.createTempFile("tasks", ".csv");

            // Test: Создадим пустой файл
            FileBackedTaskService manager = new FileBackedTaskService(tempFile);
            manager.removeAllTasks();
            manager.removeAllSubTasks();
            manager.removeAllEpics();
            System.out.println("Test 1: Пустой файл успешно создан.");

            // Test: Загрузка и сохранение задач
            Task task1 = new Task("Task 1", "Description 1");
            Epic epic1 = new Epic(0, "Epic 1", "Description 1");
            SubTask subTask1 = new SubTask(0, "SubTask 1", "Description 1", Status.NEW, epic1);

            manager.addTask(task1);
            manager.addEpic(epic1);
            manager.addSubTask(subTask1);

            FileBackedTaskService loadedManager = FileBackedTaskService.loadFromFile(tempFile);

            assert loadedManager != null;
            List<Task> tasks = loadedManager.getTasks();
            List<Epic> epics = loadedManager.getEpics();
            List<SubTask> subTasks = loadedManager.getSubTasks();

            assert tasks.size() == 1 : "Ошибка: Счетчик Task не соответствует";
            assert epics.size() == 1 : "Ошибка: Счетчик Epic не соответствует";
            assert subTasks.size() == 1 : "Ошибка: Счетчик SubTask не соответствует";

            System.out.println("Test 2: Загрузка и сохранение задач прошло успешно");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
