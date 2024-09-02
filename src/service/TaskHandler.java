package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.google.gson.GsonBuilder;

public class TaskHandler extends BaseHttpHandler {

    private final FileBackedTaskService taskService;
    private final Gson gson;

    public TaskHandler(FileBackedTaskService taskService) {
        this.taskService = taskService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())  // Регистрация адаптера для LocalDateTime
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())          // Регистрация адаптера для LocalDate
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())          // Регистрация адаптера для LocalTime
                .registerTypeAdapter(Duration.class, new DurationAdapter())            // Регистрация адаптера для Duration
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                if (path.equals("/tasks")) {
                    try {
                        List<Task> tasks = taskService.getTasks();
                        String response = gson.toJson(tasks);
                        System.out.println(response);
                        sendText(exchange, response, 200);
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendText(exchange, "Internal Server Error", 500);
                    }
                } else {
                    int id = extractIdFromPath(path);
                    Task task = taskService.getTaskById(id);
                    if (task == null) {
                        sendNotFound(exchange);
                    } else {
                        String response = gson.toJson(task);
                        System.out.println(response);
                        sendText(exchange, response, 200);
                    }
                }
                break;
            case "POST":
                try (InputStream is = exchange.getRequestBody()) {
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);

                    // Проверка валидности данных
                    if (task.getName() == null || task.getName().isEmpty() || task.getStatus() == null) {
                        sendText(exchange, "Некорректные данные задачи", 400);
                        return;
                    }

                    if (task.getId() == 0) {
                        taskService.addTask(task);
                        sendText(exchange, "Задача создана успешно", 201);
                    } else {
                        taskService.updateTask(task);
                        sendText(exchange, "Задача обновлена успешно", 200);
                    }
                } catch (IllegalArgumentException e) {
                    sendText(exchange, "Некорректные данные", 400);
                }
                break;

            case "DELETE":
                int id = extractIdFromPath(path);
                if (taskService.getTaskById(id) != null) {
                    taskService.removeTask(id);
                    sendText(exchange, "Задача удалена успешно", 200);
                } else {
                    sendText(exchange, "Задача не найдена", 404);
                }
                break;

        }
    }


    private int extractIdFromPath(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}
