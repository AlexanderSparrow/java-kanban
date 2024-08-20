import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskServiceTest extends TaskServiceTest {

    private File file;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Duration duration = Duration.ofMinutes(4);
    LocalDateTime startTime = LocalDateTime.parse("2024-08-18 10:00", formatter);

    @BeforeEach
    public void setUp() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        taskService = new FileBackedTaskService(file);
    }

    @AfterEach
    public void tearDown() {
        file.delete();
    }

    @Test
    void shouldCreateEmptyFile() {
        assertTrue(file.exists(), "Ошибка: Файл не был создан.");
    }

    @Test
    void shouldSaveAndLoadTasksCorrectly() {
        // Создаем задачи
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(4),
                startTime);
        // Добавляем задачи в менеджер
        taskService.addTask(task1);
        Epic epic1 = new Epic(2, "Epic 1", "Description 1");
        taskService.addEpic(epic1);
        SubTask subTask1 = new SubTask(3, "SubTask 1", "Description 1", Status.NEW, duration, startTime.plusMinutes(10), epic1);
        taskService.addSubTask(subTask1);

        // Загружаем менеджер из файла
        FileBackedTaskService loadedManager = FileBackedTaskService.loadFromFile(file);
        assertNotNull(loadedManager, "Ошибка: Менеджер не был загружен из файла.");

        // Проверяем, что задачи успешно восстановились
        List<Task> tasks = loadedManager.getTasks();
        List<Epic> epics = loadedManager.getEpics();
        List<SubTask> subTasks = loadedManager.getSubTasks();

        assertEquals(1, tasks.size(), "Ошибка: Счетчик Task не соответствует");
        assertEquals(1, epics.size(), "Ошибка: Счетчик Epic не соответствует");
        assertEquals(1, subTasks.size(), "Ошибка: Счетчик SubTask не соответствует");

        // Проверяем восстановленные задачи
        Task loadedTask1 = tasks.get(0);
        Epic loadedEpic1 = epics.get(0);
        SubTask loadedSubTask1 = subTasks.get(0);

        // Проверка Task
        assertEquals(task1.getName(), loadedTask1.getName(), "Ошибка: Имя Task не совпадает");
        assertEquals(task1.getDescription(), loadedTask1.getDescription(), "Ошибка: Описание Task не совпадает");
        assertEquals(task1.getStatus(), loadedTask1.getStatus(), "Ошибка: Статус Task не совпадает");
        assertEquals(task1.getId(), loadedTask1.getId(), "Ошибка: ID Task не совпадает");
        assertEquals(task1.getDuration(), loadedTask1.getDuration(), "Ошибка: Продолжительность Task не совпадает");
        assertEquals(task1.getStartTime(), loadedTask1.getStartTime(), "Ошибка: Время начала Task не совпадает");

        // Проверка Epic
        assertEquals(epic1.getName(), loadedEpic1.getName(), "Ошибка: Имя Epic не совпадает");
        assertEquals(epic1.getDescription(), loadedEpic1.getDescription(), "Ошибка: Описание Epic не совпадает");
        assertEquals(epic1.getStatus(), loadedEpic1.getStatus(), "Ошибка: Статус Epic не совпадает");
        assertEquals(epic1.getId(), loadedEpic1.getId(), "Ошибка: ID Epic не совпадает");

        // Проверка SubTask
        assertEquals(subTask1.getName(), loadedSubTask1.getName(), "Ошибка: Имя SubTask не совпадает");
        assertEquals(subTask1.getDescription(), loadedSubTask1.getDescription(), "Ошибка: Описание SubTask не совпадает");
        assertEquals(subTask1.getStatus(), loadedSubTask1.getStatus(), "Ошибка: Статус SubTask не совпадает");
        assertEquals(subTask1.getEpicId(), loadedSubTask1.getEpicId(), "Ошибка: Epic ID у SubTask не совпадает");
        assertEquals(subTask1.getId(), loadedSubTask1.getId(), "Ошибка: ID SubTask не совпадает");
        assertEquals(subTask1.getDuration(), loadedSubTask1.getDuration(), "Ошибка: Продолжительность SubTask не совпадает");
        assertEquals(subTask1.getStartTime(), loadedSubTask1.getStartTime(), "Ошибка: Время начала SubTask не совпадает");

        // Дополнительная проверка, что у эпика есть правильные подзадачи
        Set<SubTask> epicSubTasks = loadedEpic1.getSubTasks();
        assertEquals(1, epicSubTasks.size(), "Ошибка: Количество подзадач у эпика не совпадает");
        assertTrue(epicSubTasks.contains(loadedSubTask1), "Ошибка: Подзадача не привязана к эпику корректно");

        // Проверка сортировки задач в приоритетном списке
        List<Task> prioritizedTasks = new ArrayList<>(taskService.getPrioritizedTasks());
        assertEquals(2, prioritizedTasks.size(), "Ошибка: Количество задач в приоритетном списке не совпадает");

        // Проверяем, что задачи отсортированы по времени начала
        assertEquals(loadedTask1, prioritizedTasks.get(0), "Ошибка: Задачи не отсортированы правильно");
        assertEquals(loadedSubTask1, prioritizedTasks.get(1), "Ошибка: Задачи не отсортированы правильно");



    }
}
