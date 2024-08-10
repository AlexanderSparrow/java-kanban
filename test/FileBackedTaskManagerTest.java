import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskService;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskService manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskService(tempFile);
        manager.removeAllTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpics();
    }

    @Test
    void shouldCreateEmptyFile() {
        // Test 1: Пустой файл должен успешно создаться
        assertTrue(tempFile.exists(), "Ошибка: Файл не был создан.");
    }

    @Test
    void shouldSaveAndLoadTasksCorrectly() {
        // Test 2: Сохранение и загрузка задач
        Task task1 = new Task("Task 1", "Description 1");
        Epic epic1 = new Epic(0, "Epic 1", "Description 1");
        SubTask subTask1 = new SubTask(0, "SubTask 1", "Description 1", Status.NEW, epic1);

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);

        FileBackedTaskService loadedManager = FileBackedTaskService.loadFromFile(tempFile);
        assertNotNull(loadedManager, "Ошибка: Менеджер не был загружен из файла.");

        List<Task> tasks = loadedManager.getTasks();
        List<Epic> epics = loadedManager.getEpics();
        List<SubTask> subTasks = loadedManager.getSubTasks();

        assertEquals(1, tasks.size(), "Ошибка: Счетчик Task не соответствует");
        assertEquals(1, epics.size(), "Ошибка: Счетчик Epic не соответствует");
        assertEquals(1, subTasks.size(), "Ошибка: Счетчик SubTask не соответствует");

        Task loadedTask1 = tasks.getFirst();
        Epic loadedEpic1 = epics.getFirst();
        SubTask loadedSubTask1 = subTasks.getFirst();

        assertEquals(task1.getName(), loadedTask1.getName(), "Ошибка: Имя Task не совпадает");
        assertEquals(task1.getDescription(), loadedTask1.getDescription(), "Ошибка: Описание Task не совпадает");
        assertEquals(task1.getStatus(), loadedTask1.getStatus(), "Ошибка: Статус Task не совпадает");

        assertEquals(epic1.getName(), loadedEpic1.getName(), "Ошибка: Имя Epic не совпадает");
        assertEquals(epic1.getDescription(), loadedEpic1.getDescription(), "Ошибка: Описание Epic не совпадает");
        assertEquals(epic1.getStatus(), loadedEpic1.getStatus(), "Ошибка: Статус Epic не совпадает");

        assertEquals(subTask1.getName(), loadedSubTask1.getName(), "Ошибка: Имя SubTask не совпадает");
        assertEquals(subTask1.getDescription(), loadedSubTask1.getDescription(), "Ошибка: Описание SubTask не совпадает");
        assertEquals(subTask1.getStatus(), loadedSubTask1.getStatus(), "Ошибка: Статус SubTask не совпадает");
        assertEquals(subTask1.getEpicId(), loadedSubTask1.getEpicId(), "Ошибка: Epic ID у SubTask не совпадает");
    }
}
