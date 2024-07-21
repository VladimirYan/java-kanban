package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.gson.DurationTypeAdapter;
import server.gson.LocalDateTimeTypeAdapter;
import server.handler.PrioritizedTaskHandler;
import tasks.Task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PrioritizedTaskHandlerTest {
    private TaskManager taskManager;
    private Gson gson;
    private PrioritizedTaskHandler prioritizedTaskHandler;
    private HttpExchange exchange;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        taskManager = mock(TaskManager.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();

        prioritizedTaskHandler = new PrioritizedTaskHandler(taskManager, gson);
        exchange = mock(HttpExchange.class);
        outputStream = new ByteArrayOutputStream();

        when(exchange.getResponseBody()).thenReturn(outputStream);
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
    }

    @Test
    public void testHandleGetPrioritizedTasks_WhenTasksExist() throws IOException {
        Task task = new Task(1, "Test Task", Duration.ofHours(1), LocalDateTime.now());
        List<Task> tasks = Collections.singletonList(task);

        when(taskManager.getPrioritizedTasks()).thenReturn(tasks);
        when(exchange.getRequestMethod()).thenReturn("GET");

        prioritizedTaskHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(200, outputStream.size());
        assertEquals(gson.toJson(tasks), outputStream.toString());
    }

    @Test
    public void testHandleGetPrioritizedTasks_WhenNoTasksExist() throws IOException {
        when(taskManager.getPrioritizedTasks()).thenReturn(Collections.emptyList());
        when(exchange.getRequestMethod()).thenReturn("GET");

        prioritizedTaskHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(200, outputStream.size());
        assertEquals("[]", outputStream.toString());
    }

    @Test
    public void testHandle_WhenExceptionThrown() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(taskManager.getPrioritizedTasks()).thenThrow(new RuntimeException("Unexpected error"));

        prioritizedTaskHandler.handle(exchange);

        verify(exchange, times(1)).sendResponseHeaders(500, "{\"error\":\"Internal server error\"}".length());
        assertEquals("{\"error\":\"Internal server error\"}", outputStream.toString());
    }
}



