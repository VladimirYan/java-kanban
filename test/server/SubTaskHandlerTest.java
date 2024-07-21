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
import server.handler.SubTaskHandler;
import tasks.Epic;
import tasks.SubTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SubTaskHandlerTest {
    private TaskManager taskManager;
    private Gson gson;
    private SubTaskHandler subTaskHandler;
    private HttpExchange exchange;
    private ByteArrayOutputStream outputStream;

    private static final int SUBTASK_ID = 1;
    private static final String SUBTASK_NAME = "SubTask to delete";
    private static final String DELETE_MESSAGE = "{\"message\":\"Subtask deleted successfully\"}";
    private static final String DELETE_ALL_MESSAGE = "{\"message\":\"All subtasks deleted successfully\"}";
    private static final String ERROR_RESPONSE = "{\"error\":\"Internal server error\"}";

    @BeforeEach
    public void setUp() {
        taskManager = mock(TaskManager.class);
        gson = createGson();
        subTaskHandler = new SubTaskHandler(taskManager, gson);
        exchange = mock(HttpExchange.class);
        outputStream = new ByteArrayOutputStream();
        setUpExchangeMock();
    }

    private Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    private void setUpExchangeMock() {
        when(exchange.getResponseBody()).thenReturn(outputStream);
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
    }

    @Test
    public void testHandleGetSubtasks() throws IOException {
        List<SubTask> subtasks = createSubTaskList();
        when(taskManager.getAllSubTasks()).thenReturn(subtasks);
        when(exchange.getRequestMethod()).thenReturn("GET");

        subTaskHandler.handle(exchange);

        verifyResponse(gson.toJson(subtasks));
    }

    @Test
    public void testHandlePostSubtask() throws IOException {
        SubTask subTask = new SubTask(SUBTASK_ID, "New SubTask", 1, Duration.ofHours(1), LocalDateTime.now());
        String requestBody = gson.toJson(subTask);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8)));
        when(taskManager.createSubTask(any(SubTask.class))).thenReturn(subTask);

        subTaskHandler.handle(exchange);

        verify(exchange, times(1)).sendResponseHeaders(201, requestBody.length());
        assertEquals(gson.toJson(subTask), outputStream.toString());
    }

    @Test
    public void testHandleDeleteSubtaskById() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("DELETE");
        URI uri = URI.create("http://localhost/?id=" + SUBTASK_ID);
        when(exchange.getRequestURI()).thenReturn(uri);
        when(taskManager.getSubTask(SUBTASK_ID)).thenReturn(new SubTask(SUBTASK_ID, SUBTASK_NAME, 1, Duration.ofHours(1), LocalDateTime.now()));

        subTaskHandler.handle(exchange);

        verify(taskManager).deleteSubTaskById(SUBTASK_ID);
        verifyResponse(DELETE_MESSAGE);
    }

    @Test
    public void testHandleDeleteAllSubtasks() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("DELETE");
        when(exchange.getRequestURI()).thenReturn(URI.create("http://localhost/"));

        subTaskHandler.handle(exchange);

        verify(taskManager).deleteAllSubTasks();
        verifyResponse(DELETE_ALL_MESSAGE);
    }

    @Test
    public void testHandleGetSubtasks_NotFound() throws IOException {
        when(taskManager.getAllSubTasks()).thenReturn(Collections.emptyList());
        when(exchange.getRequestMethod()).thenReturn("GET");

        subTaskHandler.handle(exchange);

        verifyResponse(gson.toJson(Collections.emptyList()));
    }

    @Test
    public void testHandleInternalServerError() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        doThrow(new RuntimeException("Simulated server error"))
                .when(taskManager).getAllSubTasks();

        subTaskHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(500), anyLong());
        assertEquals(ERROR_RESPONSE, outputStream.toString(StandardCharsets.UTF_8));
    }

    private void verifyResponse(String expectedResponse) throws IOException {
        ArgumentCaptor<Integer> responseCodeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(exchange, times(1)).sendResponseHeaders(responseCodeCaptor.capture(), anyLong());
        assertEquals(200, responseCodeCaptor.getValue());
        assertEquals(expectedResponse, outputStream.toString());
    }

    private List<SubTask> createSubTaskList() {
        Epic epic = new Epic(1, "Epic 1");
        SubTask subTask1 = new SubTask(2, "SubTask 1", epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        SubTask subTask2 = new SubTask(3, "SubTask 2", epic.getId(), Duration.ofHours(2), LocalDateTime.now());
        return Arrays.asList(subTask1, subTask2);
    }
}




