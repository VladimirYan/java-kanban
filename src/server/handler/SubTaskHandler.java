package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import server.BaseHttpHandler;
import tasks.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubTaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;
    private static final Logger logger = Logger.getLogger(SubTaskHandler.class.getName());

    public SubTaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        handleGetSubTasks(exchange);
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        handlePostSubTask(exchange);
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        handleDeleteSubTask(exchange);
    }

    private void handleGetSubTasks(HttpExchange exchange) throws IOException {
        try {
            List<SubTask> subtasks = taskManager.getAllSubTasks();
            sendResponse(exchange, 200, gson.toJson(subtasks));
        } catch (Exception e) {
            handleError(exchange, e);
        }
    }

    private void handlePostSubTask(HttpExchange exchange) throws IOException {
        try {
            SubTask subtask = readRequestBody(exchange);
            SubTask createdSubTask = taskManager.createSubTask(subtask);
            sendResponse(exchange, 201, gson.toJson(createdSubTask));
        } catch (Exception e) {
            handleError(exchange, e);
        }
    }

    private void handleDeleteSubTask(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            if (isSpecificSubtaskRequest(query)) {
                deleteSpecificSubtask(exchange, query);
            } else {
                deleteAllSubtasks(exchange);
            }
        } catch (Exception e) {
            handleError(exchange, e);
        }
    }

    private void deleteSpecificSubtask(HttpExchange exchange, String query) throws IOException {
        int subtaskId = extractSubtaskId(query);
        if (taskManager.getSubTask(subtaskId) != null) {
            taskManager.deleteSubTaskById(subtaskId);
            sendResponse(exchange, 200, "{\"message\":\"Subtask deleted successfully\"}");
        } else {
            sendNotFound(exchange);
        }
    }

    private void deleteAllSubtasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllSubTasks();
            sendResponse(exchange, 200, "{\"message\":\"All subtasks deleted successfully\"}");
        } catch (Exception e) {
            handleError(exchange, e);
        }
    }

    private void handleError(HttpExchange exchange, Exception e) throws IOException {
        logger.log(Level.SEVERE, "Internal server error", e);
        sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
    }

    private boolean isSpecificSubtaskRequest(String query) {

        return query != null && query.contains("id=");
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

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        sendText(exchange, statusCode, response);
    }
}





