package handlers;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskService;

import java.io.IOException;
import java.util.List;

public class PrioritizedTaskHandler extends BaseHttpHandler {

    public PrioritizedTaskHandler(TaskService taskService) {
        super(taskService);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                // GET /prioritized - получение задач в порядке приоритета
                List<Task> prioritizedTasks = taskService.getPrioritizedTasks();
                sendText(exchange, gson.toJson(prioritizedTasks), 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Internal Server Error", 500);
        }
    }
}
