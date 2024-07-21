package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, 404, "{\"error\":\"Resource not found\"}");
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, 406, "{\"error\":\"Task overlaps with existing tasks\"}");
    }
}

