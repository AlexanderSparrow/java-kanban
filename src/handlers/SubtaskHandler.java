package handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.SubTask;
import service.TaskService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskService taskService) {
        super(taskService); // Вызов конструктора базового класса
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                handleGetRequest(exchange, path);
                break;
            case "POST":
                handlePostRequest(exchange);
                break;
            case "DELETE":
                handleDeleteRequest(exchange, path);
                break;
            default:
                sendText(exchange, "Метод не поддерживается", 405);
                break;
        }
    }

    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
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
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            if (body.isEmpty()) {  // Проверка на пустое тело
                sendText(exchange, "Тело запроса не должно быть пустым", 400);
                return;
            }

            SubTask subTask = gson.fromJson(body, SubTask.class);
            Epic epicById = taskService.getEpicById(Optional.ofNullable(subTask.getEpicIdTemp()).orElse(0));
            subTask.setEpic(epicById);

            if (subTask.getName() == null || subTask.getName().isEmpty() || subTask.getStatus() == null) {
                sendText(exchange, "Некорректные данные подзадачи", 400);
                return;
            }

            if (subTask.getId() == 0) {
                taskService.addSubTask(subTask);
                sendText(exchange, "Подзадача успешно создана", 201);
            } else {
                taskService.updateSubTask(subTask);
                sendText(exchange, "Подзадача успешно обновлена", 200);
            }
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException {
        int id = super.extractIdFromPath(path);
        if (taskService.getSubTaskById(id) != null) {
            taskService.removeSubTask(id);
            sendText(exchange, "Подзадача удалена успешно", 200);
        } else {
            sendText(exchange, "Подзадача не найдена", 404);
        }
    }
}
