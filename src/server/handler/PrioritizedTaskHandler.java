package server.handler;

import server.BaseHttpHandler;
import manager.TaskManager;
import tasks.Task;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrioritizedTaskHandler extends BaseHttpHandler {
    private static final Logger LOGGER = Logger.getLogger(PrioritizedTaskHandler.class.getName());

    private static final int STATUS_OK = 200;
    private static final int STATUS_NOT_ACCEPTABLE = 406;
    private static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedTaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        handleGetPrioritizedTasks(exchange);
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        try {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            String response = gson.toJson(prioritizedTasks);
            sendResponse(exchange, STATUS_OK, response);
        } catch (UnsupportedOperationException e) {
            LOGGER.log(Level.WARNING, "Unsupported operation: ", e);
            sendResponse(exchange, STATUS_NOT_ACCEPTABLE, "{\"error\":\"Not acceptable\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Internal server error: ", e);
            sendResponse(exchange, STATUS_INTERNAL_SERVER_ERROR, "{\"error\":\"Internal server error\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        try (var os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}







