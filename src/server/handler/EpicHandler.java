package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import server.BaseHttpHandler;
import tasks.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;
    private static final Logger logger = Logger.getLogger(EpicHandler.class.getName());

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET" -> handleGetEpics(exchange);
                case "POST" -> handlePostEpic(exchange);
                case "DELETE" -> handleDeleteEpic(exchange);
                default -> sendNotFound(exchange);
            }
        } catch (Exception e) {
            handleError(exchange, e);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendResponse(exchange, 200, gson.toJson(epics));
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        Epic epic = readRequestBody(exchange);
        Epic createdEpic = taskManager.createEpic(epic);
        sendResponse(exchange, 201, gson.toJson(createdEpic));
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (isSpecificEpicRequest(query)) {
            deleteSpecificEpic(exchange, query);
        } else {
            deleteAllEpics(exchange);
        }
    }

    private void deleteSpecificEpic(HttpExchange exchange, String query) throws IOException {
        int epicId = extractEpicId(query);

        if (taskManager.getEpic(epicId) != null) {
            taskManager.deleteEpicById(epicId);
            sendResponse(exchange, 200, "{\"message\":\"Epic deleted successfully\"}");
        } else {
            sendNotFound(exchange);
        }
    }

    private void deleteAllEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteAllEpics();
        sendResponse(exchange, 200, "{\"message\":\"All epics deleted successfully\"}");
    }

    private void handleError(HttpExchange exchange, Exception e) throws IOException {
        logger.log(Level.SEVERE, "Internal server error", e);
        sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
    }

    private boolean isSpecificEpicRequest(String query) {
        return query != null && query.contains("id=");
    }

    private int extractEpicId(String query) {
        String[] params = query.split("&");

        for (String param : params) {
            if (param.startsWith("id=")) {
                return Integer.parseInt(param.split("=")[1]);
            }
        }

        throw new IllegalArgumentException("Epic ID not found in query");
    }

    private Epic readRequestBody(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(requestBody, Epic.class);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        sendText(exchange, statusCode, response);
    }
}





