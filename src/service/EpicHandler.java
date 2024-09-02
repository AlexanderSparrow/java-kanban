package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class EpicHandler extends BaseHttpHandler {
    private final FileBackedTaskService taskService;
    private final Gson gson;

    public EpicHandler(FileBackedTaskService taskService) {
        this.taskService = taskService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())  // Регистрация адаптера для LocalDateTime
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())          // Регистрация адаптера для LocalDate
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())          // Регистрация адаптера для LocalTime
                .registerTypeAdapter(Duration.class, new DurationAdapter())            // Регистрация адаптера для Duration
                .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            System.out.println(method);//TODO
            String path = exchange.getRequestURI().getPath();
            System.out.println(path);//TODO
            String[] pathParts = path.split("/");

            if ("GET".equals(method)) {
                handleGetRequest(exchange, pathParts);
            } else if ("POST".equals(method)) {
                handlePostRequest(exchange);
            } else if ("DELETE".equals(method) && pathParts.length == 3) {
                handleDeleteRequest(exchange, pathParts);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handleGetRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            // GET /epics - получение всех эпиков
            List<Epic> epics = taskService.getEpics();
            sendText(exchange, gson.toJson(epics), 200);
        } else if (pathParts.length == 3) {
            // GET /epics/{id} - получение эпика по id
            int id = Integer.parseInt(pathParts[2]);
            Epic epic = taskService.getEpicById(id);
            if (epic != null) {
                sendText(exchange, gson.toJson(epic), 200);
            } else {
                sendNotFound(exchange);
            }
        } else if (pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
            // GET /epics/{id}/subtasks - получение подзадач эпика по id
            int id = Integer.parseInt(pathParts[2]);
            Set<SubTask> subtasks = taskService.getAllSubTasksByEpic(id);
            if (subtasks != null) {
                sendText(exchange, gson.toJson(subtasks), 200);
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);
        if (epic.getId() == 0 || taskService.getEpicById(epic.getId()) == null) {
            taskService.addEpic(epic);
            sendText(exchange, "Epic created successfully", 201);
        } else {
            taskService.updateEpic(epic);
            sendText(exchange, "Epic updated successfully", 200);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        int id = Integer.parseInt(pathParts[2]);
        taskService.removeEpic(id);
        sendText(exchange, "Epic deleted successfully", 200);
    }
}
