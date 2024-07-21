package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import server.BaseHttpHandler;
import tasks.SubTask;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    private static final int STATUS_OK = 200;
    private static final int STATUS_CREATED = 201;
    private static final int STATUS_ERROR = 500;
    private static final String MESSAGE_SUBTASK_DELETED = "{\"message\":\"Subtask deleted successfully\"}";
    private static final String MESSAGE_ALL_SUBTASKS_DELETED = "{\"message\":\"All subtasks deleted successfully\"}";
    private static final String ERROR_INTERNAL_SERVER = "{\"error\":\"Internal server error\"}";

    public SubTaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET" -> handleGetSubtasks(exchange);
                case "POST" -> handlePostSubtask(exchange);
                case "DELETE" -> handleDeleteSubtask(exchange);
                default -> sendNotFound(exchange);
            }
        } catch (Exception e) {
            handleError(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        List<SubTask> subtasks = taskManager.getAllSubTasks();
        String response = gson.toJson(subtasks);
        sendResponse(exchange, STATUS_OK, response);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        SubTask subtask = readRequestBody(exchange);
        SubTask createdSubTask = taskManager.createSubTask(subtask);
        sendResponse(exchange, STATUS_CREATED, gson.toJson(createdSubTask));
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (isSpecificSubtaskRequest(query)) {
            deleteSpecificSubtask(exchange, query);
        } else {
            deleteAllSubtasks(exchange);
        }
    }

    private boolean isSpecificSubtaskRequest(String query) {
        return query != null && query.startsWith("id=");
    }

    private void deleteSpecificSubtask(HttpExchange exchange, String query) throws IOException {
        int subtaskId = extractSubtaskId(query);
        if (taskManager.getSubTask(subtaskId) != null) {
            taskManager.deleteSubTaskById(subtaskId);
            sendResponse(exchange, STATUS_OK, MESSAGE_SUBTASK_DELETED);
        } else {
            sendNotFound(exchange);
        }
    }

    private void deleteAllSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubTasks();
        sendResponse(exchange, STATUS_OK, MESSAGE_ALL_SUBTASKS_DELETED);
    }

    private int extractSubtaskId(String query) {
        String[] params = query.split("&");
        for (String param : params) {
            if (param.startsWith("id=")) {
                return Integer.parseInt(param.split("=")[1]);
            }

        }
        throw new IllegalArgumentException("Subtask ID not found in query");
    }

    private SubTask readRequestBody(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(requestBody, SubTask.class);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
        exchange.sendResponseHeaders(statusCode, responseBody.length());
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void handleError(HttpExchange exchange) throws IOException {
        sendResponse(exchange, STATUS_ERROR, ERROR_INTERNAL_SERVER);
    }
}





