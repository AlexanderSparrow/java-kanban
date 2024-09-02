package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Такой задачи/подзадачи/эпика нет", 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "Задача пересекается с существующей", 406);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // TODO
    }
}
