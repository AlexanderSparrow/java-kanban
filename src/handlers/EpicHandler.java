package handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.SubTask;
import service.TaskService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskService taskService) {
        super(taskService); // Вызов конструктора базового класса
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
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
            List<Epic> epics = taskService.getEpics();
            sendText(exchange, gson.toJson(epics), 200);
        } else if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            Epic epic = taskService.getEpicById(id);
            if (epic != null) {
                sendText(exchange, gson.toJson(epic), 200);
            } else {
                sendNotFound(exchange);
            }
        } else if (pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
            int id = Integer.parseInt(pathParts[2]);
            Set<SubTask> subtasks = taskService.getAllSubTasksByEpic(id);
                sendText(exchange, gson.toJson(subtasks), 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            if (body.isEmpty()) {  // Проверка на пустое тело
                sendText(exchange, "Тело запроса не должно быть пустым", 400);
                return;
            }

            Epic epic = gson.fromJson(body, Epic.class);

            if (epic.getName() == null || epic.getName().isEmpty() || epic.getStatus() == null) {
                sendText(exchange, "Некорректные данные эпика", 400);
                return;
            }

            int id;
            if (epic.getId() == 0 || taskService.getEpicById(epic.getId()) == null) {
                id = taskService.addEpic(epic);
                sendText(exchange, Integer.toString(id), 201);
            } else {
                id = taskService.updateEpic(epic);
                sendText(exchange, "Эпик успешно обновлен", 200);
            }
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        int id = Integer.parseInt(pathParts[2]);
        if (taskService.getEpicById(id) != null) {
            taskService.removeEpic(id);
            sendText(exchange, "Epic удален успешно", 200);
        } else {
            sendText(exchange, "Задача не найдена", 404);
        }
    }
}
