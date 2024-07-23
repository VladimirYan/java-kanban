package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import server.BaseHttpHandler;
import tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;
    private static final Logger logger = Logger.getLogger(HistoryHandler.class.getName());

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        handleGetHistory(exchange);
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        try {
            List<Task> historyTasks = taskManager.getHistory();
            String response = gson.toJson(historyTasks);
            sendText(exchange, 200, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Internal server error", e);
            sendText(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
}






