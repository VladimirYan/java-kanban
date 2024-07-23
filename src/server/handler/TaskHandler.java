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
    protected void processGet(HttpExchange exchange) throws IOException {
        handleGetTasks(exchange);
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        handlePostTask(exchange);
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        handleDeleteTask(exchange);
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        try {
            List<Task> tasks = taskManager.getAllTasks();
            String response = gson.toJson(tasks);
            sendText(exchange, 200, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Internal server error", e);
            sendText(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            String requestBody = readRequestBody(exchange);
            Task task = gson.fromJson(requestBody, Task.class);
            Task createdTask = taskManager.createTask(task);
            respondToCreateTask(exchange, createdTask);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Internal server error", e);
            sendText(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("id=")) {
                deleteTaskById(exchange, query.split("=")[1]);
            } else {
                deleteAllTasks(exchange);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Internal server error", e);
            sendText(exchange, 500, "{\"error\":\"Internal server error\"}");
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
}





