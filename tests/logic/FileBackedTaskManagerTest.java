package logic;

import manager.*;
import tasks.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManagerTest {
    private Path tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = Files.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(new InMemoryHistoryManager(), tempFile.toFile());
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    //Проверка корректности сохранения и загрузки задач из файла.
    @Test
    public void testSaveAndLoad() {
        Task task = new Task(1, "Task 1");
        Epic epic = new Epic(2, "Epic 1");
        SubTask subTask = new SubTask(3, "SubTask 1", epic.getId());

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubTask(subTask);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile.toFile());

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllTasks().size());

        assertEquals(task.getId(), loadedManager.getTask(task.getId()).getId());
        assertEquals(epic.getId(), loadedManager.getEpic(epic.getId()).getId());
        assertEquals(subTask.getId(), loadedManager.getSubTask(subTask.getId()).getId());
    }

    //Проверка корректности преобразования задачи в строку и обратно.
    @Test
    public void testTaskToStringAndFromString() {
        Task task = new Task(1, "Task 1");
        String taskString = manager.taskToString(task);
        Task parsedTask = manager.taskFromString(taskString);

        assertEquals(task.getId(), parsedTask.getId());
        assertEquals(task.getName(), parsedTask.getName());
        assertEquals(task.getStatus(), parsedTask.getStatus());
    }

    //Проверка корректности преобразования эпика в строку и обратно.
    @Test
    public void testEpicToStringAndFromString() {
        Epic epic = new Epic(2, "Epic 1");
        String epicString = manager.taskToString(epic);
        Task parsedEpic = manager.taskFromString(epicString);

        assertEquals(epic.getId(), parsedEpic.getId());
        assertEquals(epic.getName(), parsedEpic.getName());
        assertEquals(epic.getStatus(), parsedEpic.getStatus());
    }

    //Проверка корректности преобразования подзадачи в строку и обратно.
    @Test
    public void testSubTaskToStringAndFromString() {
        SubTask subTask = new SubTask(3, "SubTask 1", 1);
        String subTaskString = manager.taskToString(subTask);
        Task parsedSubTask = manager.taskFromString(subTaskString);

        assertEquals(subTask.getId(), parsedSubTask.getId());
        assertEquals(subTask.getName(), parsedSubTask.getName());
        assertEquals(subTask.getStatus(), parsedSubTask.getStatus());
        assertInstanceOf(SubTask.class, parsedSubTask);
        assertEquals(((SubTask) parsedSubTask).getEpicId(), subTask.getEpicId());
    }

    //Проверка корректности загрузки из несуществующего файла.
    @Test
    public void testLoadWithNonExistingFile() {
        File nonExistingFile = new File("non-existing-file.csv");
        FileBackedTaskManager managerWithNonExistingFile = FileBackedTaskManager.loadFromFile(nonExistingFile);

        assertTrue(managerWithNonExistingFile.getAllTasks().isEmpty());
        assertTrue(managerWithNonExistingFile.getAllEpics().isEmpty());
        assertTrue(managerWithNonExistingFile.getAllSubTasks().isEmpty());
    }
}