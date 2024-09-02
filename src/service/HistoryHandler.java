package service;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskService taskService;
    private final Gson gson;

    public HistoryHandler(TaskService taskService) {
        this.taskService = taskService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())  // Регистрация адаптера для LocalDateTime
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())          // Регистрация адаптера для LocalDate
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())          // Регистрация адаптера для LocalTime
                .registerTypeAdapter(Duration.class, new DurationAdapter())            // Регистрация адаптера для Duration
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                // GET /history - получение истории задач
                List<Task> history = taskService.getHistory();
                sendText(exchange, gson.toJson(history), 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Internal Server Error", 500);
        }
    }
}
