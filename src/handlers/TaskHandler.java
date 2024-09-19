package handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.FileBackedTaskService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(FileBackedTaskService taskService) {
        super(taskService);  // Вызов конструктора базового класса
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                handleGet(exchange, path);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                handleDelete(exchange, path);
                break;
            default:
                sendText(exchange, "Метод не поддерживается", 405);
                break;
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
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
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            if (body.isEmpty()) {  // Проверка на пустое тело
                sendText(exchange, "Тело запроса не должно быть пустым", 400);
                return;
            }

            Task task = gson.fromJson(body, Task.class);

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
            sendHasInteractions(exchange);
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        int id = extractIdFromPath(path);
        if (taskService.getTaskById(id) != null) {
            taskService.removeTask(id);
            sendText(exchange, "Задача удалена успешно", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}
