package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {

    private final FileBackedTaskService taskService;
    private final Gson gson;

    public SubtaskHandler(FileBackedTaskService taskService) {
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
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                if (path.equals("/subtasks")) {
                    try {
                        List<SubTask> subTasks = taskService.getSubTasks();
                        String response = gson.toJson(subTasks);
                        System.out.println(response);
                        sendText(exchange, response, 200);
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendText(exchange, "Internal Server Error", 500);
                    }
                } else {
                    int id = extractIdFromPath(path);
                    SubTask subTask = taskService.getSubTaskById(id);
                    if (subTask == null) {
                        sendNotFound(exchange);
                    } else {
                        String response = gson.toJson(subTask);
                        sendText(exchange, response, 200);
                    }
                }
                break;
            case "POST":
                try (InputStream is = exchange.getRequestBody()) {
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    SubTask subTask = gson.fromJson(body, SubTask.class);
                    Epic epicById = taskService.getEpicById(Optional.ofNullable(subTask.getEpicIdTemp()).orElse(0));
                    subTask.setEpic(epicById);
                    // Проверка валидности данных
                    if (subTask.getName() == null || subTask.getName().isEmpty() || subTask.getStatus() == null) {
                        sendText(exchange, "Некорректные данные задачи", 400);
                        return;
                    }
                    if (subTask.getId() == 0) {
                        taskService.addSubTask(subTask);
                        sendText(exchange, "Подзадача успешно создана", 201);
                    } else {
                        taskService.updateSubTask(subTask);
                        sendText(exchange, "Подзадача успешно обновлена", 201);
                    }
                } catch (IllegalArgumentException e) {
                    sendHasInteractions(exchange);
                } catch (Exception e) {
                    System.out.println(e);
                    throw e;
                }
                break;
            case "DELETE":
                int id = extractIdFromPath(path);
                if (taskService.getSubTaskById(id) != null) {
                    taskService.removeSubTask(id);
                    sendText(exchange, "Подзадача удалена успешно", 200);
                } else {
                    sendText(exchange, "Подзадача не найдена", 404);
                }
                break;

        }
    }

    private int extractIdFromPath(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}
