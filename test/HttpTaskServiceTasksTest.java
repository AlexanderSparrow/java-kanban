import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import service.*;
import model.Task;
import model.Status;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServiceTasksTest {

    private final FileBackedTaskService manager;
    private HttpTaskServer taskServer;
    private final Gson gson;

    public HttpTaskServiceTasksTest() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        this.manager = new FileBackedTaskService(file);
        this.taskServer = new HttpTaskServer(manager);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    // Тест на добавление задачи
    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(0, "Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    // Тест на получение всех задач
    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task(0, "Task 1", "First task", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task(0, "Task 2", "Second task", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(20));
        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = gson.fromJson(response.body(), List.class);
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }

    // Тест на удаление задачи
    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task(0, "Task to delete", "This task will be deleted", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now());
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertTrue(manager.getTasks().isEmpty(), "Задача не была удалена");
    }

    // Тест на добавление задачи с неправильными данными (граничный случай)
    @Test
    public void testAddTaskWithInvalidData() throws IOException, InterruptedException {
        String invalidTaskJson = "{\"name\":\"Invalid Task\",\"status\":\"INVALID_STATUS\"}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(invalidTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Должен возвращаться код 400 при отправке неправильных данных");
    }

    // Тест на удаление несуществующей задачи (граничный случай)
    @Test
    public void testDeleteNonExistingTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/9999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Должен возвращаться код 404 при удалении несуществующей задачи");
    }

    // Тест на обновление задачи
    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task(0, "Task to update", "This task will be updated", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now());
        manager.addTask(task);

        // Обновляем задачу
        task.setName("Updated Task");
        String updatedTaskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа должен быть 200 при успешном обновлении задачи");

        Task updatedTask = manager.getTaskById(task.getId());
        assertEquals("Updated Task", updatedTask.getName(), "Задача должна быть обновлена с новым именем");
    }
}
