package servers;

import com.sun.net.httpserver.HttpServer;
import handlers.*;
import service.FileBackedTaskService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final HttpServer server;

    private static final int PORT = 8080;
    public HttpTaskServer(FileBackedTaskService taskService) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Привязываем обработчики к путям
        server.createContext("/tasks", new TaskHandler(taskService));
        server.createContext("/subtasks", new SubtaskHandler(taskService));
        server.createContext("/epics", new EpicHandler(taskService));
        server.createContext("/history", new HistoryHandler(taskService));
        server.createContext("/prioritized", new PrioritizedTaskHandler(taskService));
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
    }
}
