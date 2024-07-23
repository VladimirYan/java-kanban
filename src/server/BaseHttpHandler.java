package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                processGet(exchange);
                break;
            case "POST":
                processPost(exchange);
                break;
            case "PUT":
                processPut(exchange);
                break;
            case "DELETE":
                processDelete(exchange);
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    protected void processGet(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void processPost(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void processPut(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void processDelete(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendText(exchange, 405, "{\"error\":\"Method not allowed\"}");
    }

    protected void sendText(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, 404, "{\"error\":\"Resource not found\"}");
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, 406, "{\"error\":\"Task overlaps with existing tasks\"}");
    }
}



