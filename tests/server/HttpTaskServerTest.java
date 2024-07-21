package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Managers;
import manager.TaskManager;
import server.handler.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.gson.DurationTypeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private HttpTaskServer server;

    @BeforeEach
    public void setUp() throws IOException {
        server = new HttpTaskServer();
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testServerStart() throws IOException, URISyntaxException {
        assertEquals(200, sendGetRequest("/tasks"));
        assertEquals(200, sendGetRequest("/subtasks"));
        assertEquals(200, sendGetRequest("/epics"));
        assertEquals(200, sendGetRequest("/history"));
        assertEquals(200, sendGetRequest("/prioritized"));

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        TaskManager taskManager = Managers.getDefaultInMemoryManager();

        TaskHandler taskHandler = new TaskHandler(taskManager, gson);
        SubTaskHandler subTaskHandler = new SubTaskHandler(taskManager, gson);
        EpicHandler epicHandler = new EpicHandler(taskManager, gson);
        HistoryHandler historyHandler = new HistoryHandler(taskManager, gson);
        PrioritizedTaskHandler prioritizedTaskHandler = new PrioritizedTaskHandler(taskManager, gson);

        assertEquals(taskHandler.getClass(), TaskHandler.class);
        assertEquals(subTaskHandler.getClass(), SubTaskHandler.class);
        assertEquals(epicHandler.getClass(), EpicHandler.class);
        assertEquals(historyHandler.getClass(), HistoryHandler.class);
        assertEquals(prioritizedTaskHandler.getClass(), PrioritizedTaskHandler.class);
    }

    @Test
    public void testTaskHandler() throws IOException, URISyntaxException {
        assertEquals(200, sendGetRequest("/tasks"));
    }

    @Test
    public void testSubTaskHandler() throws IOException, URISyntaxException {
        assertEquals(200, sendGetRequest("/subtasks"));
    }

    @Test
    public void testEpicHandler() throws IOException, URISyntaxException {
        assertEquals(200, sendGetRequest("/epics"));
    }

    @Test
    public void testHistoryHandler() throws IOException, URISyntaxException {
        assertEquals(200, sendGetRequest("/history"));
    }

    @Test
    public void testPrioritizedTaskHandler() throws IOException, URISyntaxException {
        assertEquals(200, sendGetRequest("/prioritized"));
    }

    private int sendGetRequest(String path) throws IOException, URISyntaxException {
        URL url = new URL(new URI("http", null, "localhost", 8080, path, null, null).toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        return responseCode;
    }
}

