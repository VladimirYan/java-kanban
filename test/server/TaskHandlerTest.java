package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import server.gson.DurationTypeAdapter;
import server.gson.LocalDateTimeTypeAdapter;
import server.handler.TaskHandler;
import tasks.Task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TaskHandlerTest {
    private TaskManager taskManager;
    private Gson gson;
    private TaskHandler taskHandler;
    private HttpExchange exchange;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        taskManager = mock(TaskManager.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
        taskHandler = new TaskHandler(taskManager, gson);
        exchange = mock(HttpExchange.class);
        outputStream = new ByteArrayOutputStream();

        when(exchange.getResponseBody()).thenReturn(outputStream);
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
    }

    @Test
    public void testHandleGetTasks() throws IOException {
        Task task = new Task(1, "Test Task", Duration.ofHours(1), LocalDateTime.now());
        when(taskManager.getAllTasks()).thenReturn(Collections.singletonList(task));
        when(exchange.getRequestMethod()).thenReturn("GET");

        taskHandler.handle(exchange);

        ArgumentCaptor<Integer> responseCodeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(exchange, times(1)).sendResponseHeaders(responseCodeCaptor.capture(), anyLong());
        assertEquals(200, responseCodeCaptor.getValue());
        assertEquals(gson.toJson(Collections.singletonList(task)), outputStream.toString());
    }

    @Test
    public void testHandlePostTask() throws IOException {
        Task task = new Task(1, "Test Task", Duration.ofHours(1), LocalDateTime.now());
        String requestBody = gson.toJson(task);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8)));
        when(taskManager.createTask(any(Task.class))).thenReturn(task);

        taskHandler.handle(exchange);

        verify(exchange, times(1)).sendResponseHeaders(201, requestBody.length());
        assertEquals(gson.toJson(task), outputStream.toString());
    }

    @Test
    public void testHandleDeleteTask_Specific() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("DELETE");
        URI uri = URI.create("http://localhost/?id=1");
        when(exchange.getRequestURI()).thenReturn(uri);
        when(taskManager.getTask(1)).thenReturn(new Task(1, "Test Task", Duration.ofHours(1), LocalDateTime.now()));

        taskHandler.handle(exchange);

        verify(taskManager).deleteTaskById(1);
        verify(exchange, times(1)).sendResponseHeaders(200, "{\"message\":\"Task deleted successfully\"}".length());
        assertEquals("{\"message\":\"Task deleted successfully\"}", outputStream.toString());
    }

    @Test
    public void testHandleDeleteTask_All() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("DELETE");

        when(exchange.getRequestURI()).thenReturn(URI.create("http://localhost/"));

        taskHandler.handle(exchange);

        verify(taskManager).deleteAllTasks();
        verify(exchange, times(1)).sendResponseHeaders(200, "{\"message\":\"All tasks deleted successfully\"}".length());
        assertEquals("{\"message\":\"All tasks deleted successfully\"}", outputStream.toString());
    }
}






