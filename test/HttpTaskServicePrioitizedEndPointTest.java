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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class HttpTaskServicePrioitizedEndPointTest {

    private final FileBackedTaskService manager;
    private final HttpTaskServer taskServer;
    private final Gson gson;

    public HttpTaskServicePrioitizedEndPointTest() throws IOException {
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

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        // Создаём несколько задач
        Task task1 = new Task(0, "Task1", "Description 1", Status.NEW, Duration.ofMinutes(1),
                LocalDateTime.now());
        Task task2 = new Task(0, "Task2", "Description 2", Status.NEW, Duration.ofMinutes(10),
                LocalDateTime.now().plusMinutes(10));

        manager.addTask(task1);
        manager.addTask(task2);

        // Запрашиваем приоритезированные задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode());

        // Определяем тип для десериализации
        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();

        // Десериализуем JSON в список задач
        List<Task> prioritizedTasksFromManager = gson.fromJson(response.body(), taskListType);


        assertNotNull(prioritizedTasksFromManager, "Приоритезированные задачи не возвращаются");
        assertEquals(2, prioritizedTasksFromManager.size(), "Некорректное количество приоритезированных задач");
        assertEquals("Task2", prioritizedTasksFromManager.get(1).getName(), "Некорректный порядок приоритезированных задач");
    }


}
