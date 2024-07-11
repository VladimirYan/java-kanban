package logic;

import manager.*;
import tasks.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private File file;
    Logger logger = Logger.getLogger(FileBackedTaskManager.class.getName());

    @BeforeEach
    void setUp() throws IOException {
        file = Files.createTempFile("tasks", ".csv").toFile();
        taskManager = FileBackedTaskManager.loadFromFile(file);
    }

    @AfterEach
    void tearDown() {
        if (!file.delete()) {
            System.out.println("Не удалось удалить файл: " + file.getPath());
        }
    }

    @Test
    void shouldSaveAndLoadTasksCorrectly() {
        Task task = new Task(1, "Test Task");
        task.setDuration(Duration.ofHours(1));
        task.setStartTime(LocalDateTime.now());
        taskManager.createTask(task);
        taskManager.save();

        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(file);
        Task loadedTask = loadedTaskManager.getTask(1);

        assertEquals(task.getName(), loadedTask.getName(), "Задача должна быть загружена с корректным именем");
        assertEquals(task.getDuration(), loadedTask.getDuration(), "Задача должна быть загружена с корректной продолжительностью");
        assertEquals(task.getStartTime(), loadedTask.getStartTime(), "Задача должна быть загружена с корректным временем начала");
    }

    @Test
    void shouldHandleEmptyFileGracefully() {
        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(new File("nonexistent.csv"));
        assertNotNull(loadedTaskManager, "Должен быть создан экземпляр даже если файл отсутствует");
    }

    @Test
    void saveTasksCorrectly() {
        Task task = new Task(1, "Test Task");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofHours(1));
        taskManager.createTask(task);

        taskManager.save();

        assertTrue(file.exists());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            String savedTaskLine = reader.readLine();

            assertNotNull(header);
            assertNotNull(savedTaskLine);

            assertEquals("id,type,name,status,epicId,startTime,duration", header);

            String expectedTaskLine = taskManager.taskToString(task);
            assertEquals(expectedTaskLine, savedTaskLine);
        } catch (IOException e) {
            logger.severe("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    @Test
    public void taskToStringCorrectlyFormatsTask() {
        Task task = new Task(1, "Test Task");
        task.setStatus(TaskStatus.NEW);
        LocalDateTime startTime = LocalDateTime.of(2024, 7, 12, 1, 14, 20)
                .withNano(826053000);
        task.setStartTime(startTime);
        task.setDuration(Duration.ofHours(1));

        String taskLine = taskManager.taskToString(task);

        String expectedTaskLine = "1,TASK,Test Task,NEW,,2024-07-12 01:14:20.826053000,60";
        assertEquals(expectedTaskLine, taskLine);
    }

    @Test
    public void taskFromStringCreatesCorrectTask() {
        String taskString = "1,TASK,Test Task,NEW,,2024-07-12 01:14:20.826053000,60";
        Task task = taskManager.taskFromString(taskString);

        assertNotNull(task);
        assertEquals(1, task.getId());
        assertEquals(TaskType.TASK, task.getType());
        assertEquals("Test Task", task.getName());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(LocalDateTime.of(2024, 7, 12, 1, 14, 20, 826053000), task.getStartTime());
        assertEquals(Duration.ofMinutes(60), task.getDuration());
    }


}