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
import server.handler.EpicHandler;
import tasks.Epic;

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

public class EpicHandlerTest {
    private TaskManager taskManager;
    private Gson gson;
    private EpicHandler epicHandler;
    private HttpExchange exchange;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = mock(TaskManager.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
        epicHandler = new EpicHandler(taskManager, gson);
        exchange = mock(HttpExchange.class);
        outputStream = new ByteArrayOutputStream();

        when(exchange.getResponseBody()).thenReturn(outputStream);

        Headers headers = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(headers);
    }

    private String getResponse() {
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void handleGetEpics_ShouldReturnListOfEpics() throws IOException {
        Epic epic = new Epic(1, "Test Epic");
        when(taskManager.getAllEpics()).thenReturn(Collections.singletonList(epic));
        when(exchange.getRequestMethod()).thenReturn("GET");

        epicHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        assertEquals(gson.toJson(Collections.singletonList(epic)), getResponse());
    }

    @Test
    public void handlePostEpic_ShouldCreateEpic() throws IOException {
        Epic epic = new Epic(1, "Test Epic");
        String requestBody = gson.toJson(epic);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8)));
        when(taskManager.createEpic(any(Epic.class))).thenReturn(epic);

        epicHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(201), anyLong());
        assertEquals(gson.toJson(epic), getResponse());
    }

    @Test
    public void handleDeleteEpic_ShouldDeleteSpecificEpic() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("DELETE");
        when(exchange.getRequestURI()).thenReturn(URI.create("/epics?id=1"));
        when(taskManager.getEpic(1)).thenReturn(new Epic(1, "Epic 1"));

        epicHandler.handle(exchange);

        verify(taskManager).deleteEpicById(1);
        verify(exchange).sendResponseHeaders(200, "{\"message\":\"Epic deleted successfully\"}".getBytes(StandardCharsets.UTF_8).length);
        assertEquals("{\"message\":\"Epic deleted successfully\"}", getResponse());
    }

    @Test
    public void handleDeleteAllEpics_ShouldDeleteAllEpics() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("DELETE");
        when(exchange.getRequestURI()).thenReturn(URI.create("/epics"));

        epicHandler.handle(exchange);

        verify(taskManager).deleteAllEpics();
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        assertEquals("{\"message\":\"All epics deleted successfully\"}", getResponse());
    }
}



