package endPointsTests;

import adapters.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import service.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServiceHistoryEndPointTest {

    private final FileBackedTaskService manager;
    private final HttpTaskServer taskServer;
    private final Gson gson;

    public HttpTaskServiceHistoryEndPointTest() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        this.manager = new FileBackedTaskService(file);
        this.taskServer = new HttpTaskServer(manager);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())  // Регистрация адаптера для LocalDateTime
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
    public void testGetHistory() throws IOException, InterruptedException {
        // Создаём задачу
        Task task = new Task(0, "Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        // Запрашиваем историю
        URI url2 = URI.create("http://localhost:8080/history");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode());

        // Проверяем, что в истории есть задача
        List<Task> historyFromManager = gson.fromJson(response.body(), List.class);

        assertNotNull(historyFromManager, "История задач не возвращается");
        assertEquals(1, historyFromManager.size(), "Некорректное количество задач в истории");
    }
}
