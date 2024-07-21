package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import server.BaseHttpHandler;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;
    private static final Logger logger = Logger.getLogger(TaskHandler.class.getName());

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET" -> handleGetTasks(exchange);
                case "POST" -> handlePostTask(exchange);
                case "DELETE" -> handleDeleteTask(exchange);
                default -> sendNotFound(exchange);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Internal server error", e);
            sendText(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        String response = gson.toJson(tasks);
        sendText(exchange, 200, response);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        Task task = gson.fromJson(requestBody, Task.class);
        Task createdTask = taskManager.createTask(task);
        respondToCreateTask(exchange, createdTask);
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            deleteTaskById(exchange, query.split("=")[1]);
        } else {
            deleteAllTasks(exchange);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void respondToCreateTask(HttpExchange exchange, Task createdTask) throws IOException {
        if (createdTask == null) {
            sendHasInteractions(exchange);
        } else {
            sendText(exchange, 201, gson.toJson(createdTask));
        }
    }

    private void deleteTaskById(HttpExchange exchange, String idParam) throws IOException {
        int taskId = Integer.parseInt(idParam);
        if (taskManager.getTask(taskId) != null) {
            taskManager.deleteTaskById(taskId);
            sendText(exchange, 200, "{\"message\":\"Task deleted successfully\"}");
        } else {
            sendNotFound(exchange);
        }
    }

    private void deleteAllTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTasks();
        sendText(exchange, 200, "{\"message\":\"All tasks deleted successfully\"}");
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }
}





