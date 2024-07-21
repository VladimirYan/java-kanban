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
import server.handler.HistoryHandler;
import tasks.Task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class HistoryHandlerTest {

    private TaskManager taskManager;
    private Gson gson;
    private HistoryHandler historyHandler;
    private HttpExchange exchange;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        taskManager = mock(TaskManager.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();

        historyHandler = new HistoryHandler(taskManager, gson);
        exchange = mock(HttpExchange.class);
        outputStream = new ByteArrayOutputStream();

        when(exchange.getResponseBody()).thenReturn(outputStream);
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
    }

    @Test
    public void testHandleGetHistory() throws IOException {
        Task task = new Task(1, "Test Task", Duration.ofHours(1), LocalDateTime.now());

        when(taskManager.getHistory()).thenReturn(Collections.singletonList(task));
        when(exchange.getRequestMethod()).thenReturn("GET");

        historyHandler.handle(exchange);

        ArgumentCaptor<Integer> responseCodeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(exchange, times(1)).sendResponseHeaders(responseCodeCaptor.capture(), anyLong());

        assertEquals(200, responseCodeCaptor.getValue());
        assertEquals(gson.toJson(Collections.singletonList(task)), outputStream.toString());
    }

    @Test
    public void testHandleInternalServerError() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        doThrow(new RuntimeException("Test exception")).when(taskManager).getHistory();

        historyHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(500, "{\"error\":\"Internal server error\"}".getBytes().length);
    }

    @Test
    public void testHandleException() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(taskManager.getHistory()).thenThrow(new RuntimeException("Test Exception"));

        historyHandler.handle(exchange);

        assertEquals("{\"error\":\"Internal server error\"}", outputStream.toString());
        verify(exchange).sendResponseHeaders(eq(500), anyLong());
    }
}




