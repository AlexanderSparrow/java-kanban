import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

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

public class HttpTaskServiceSubTasksTest {

    private final FileBackedTaskService manager;
    private final HttpTaskServer taskServer;
    private final Gson gson;

    public HttpTaskServiceSubTasksTest() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        this.manager = new FileBackedTaskService(file);
        this.taskServer = new HttpTaskServer(manager);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())  // Регистрация адаптера для LocalDateTime
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())          // Регистрация адаптера для LocalDate
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())          // Регистрация адаптера для LocalTime
                .registerTypeAdapter(Duration.class, new DurationAdapter())            // Регистрация адаптера для Duration
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
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic testEpic = new Epic(0, "test", "test", Duration.ofMinutes(5), LocalDateTime.now(), LocalDateTime.now());
        String epicJson = gson.toJson(testEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/epics");
        HttpRequest EpicRequest = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> epicResponse = client.send(EpicRequest, HttpResponse.BodyHandlers.ofString());
        Epic epicById = manager.getEpicById(Integer.parseInt(epicResponse.body()));
        SubTask subTask = new SubTask(0, "SubTask 2", "Testing SubTask 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epicById);
        String subTaskJson = gson.toJson(subTask);
        //HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest subTaskRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> subTaskResponse = client.send(subTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subTaskResponse.statusCode());

        List<SubTask> tasksFromManager = manager.getSubTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("SubTask 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    private static class Response {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    // Тест на получение всех задач
    @Test
    public void testGetSubTasks() throws IOException, InterruptedException {
        Epic testEpic = new Epic(100, "test", "test");
        Task subTask1 = new SubTask(0, "SubTask 1", "First SubTask", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now(), testEpic);
        Task subTask2 = new SubTask(0, "SubTask 2", "Second SubTask", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(20), testEpic);
        manager.addTask(subTask1);
        manager.addTask(subTask2);

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
    public void testDeleteSubTask() throws IOException, InterruptedException {

        Epic testEpic = new Epic(100, "test", "test");
        Task subTask = new SubTask(0, "SubTask to delete", "This SubTask will be deleted", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now(), testEpic);
        manager.addTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertTrue(manager.getSubTasks().isEmpty(), "Задача не была удалена");
    }

    // Тест на добавление задачи с неправильными данными (граничный случай)
    @Test
    public void testAddSubtaskWithInvalidData() throws IOException, InterruptedException {
        String invalidSubtaskJson = "{\"name\":\"Invalid Subtask\",\"status\":\"INVALID_STATUS\"}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(invalidSubtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Должен возвращаться код 400 при отправке неправильных данных");
    }

    @Test
    public void testDeleteNonExistingSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/9999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Должен возвращаться код 404 при удалении несуществующей подзадачи");
    }

}
