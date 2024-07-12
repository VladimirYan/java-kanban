package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Duration;


import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    private final int id = 1;
    private final String name = "Test Epic";

    @BeforeEach
    void setUp() {
        epic = new Epic(id, name);
    }

    @Test
    void testConstructor() {
        assertEquals(id, epic.getId());
        assertEquals(name, epic.getName());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void testGetEndTime() {
        assertNull(epic.getEndTime());
    }

    @Test
    void testSetEndTime() {
        LocalDateTime endTime = LocalDateTime.now();
        epic.setEndTime(endTime);
        assertEquals(endTime, epic.getEndTime());
    }

    @Test
    void testAddSubTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        SubTask subTask = new SubTask(2, "Test SubTask", epic.getId(), duration, startTime);
        epic.addSubTask(subTask);
        assertTrue(epic.getSubTasks().contains(subTask));
    }

    @Test
    void testAddSubTaskWithSameId() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        SubTask subTask = new SubTask(epic.getId(), "Test SubTask", epic.getId(), duration, startTime);
        assertThrows(IllegalArgumentException.class, () -> epic.addSubTask(subTask));
    }

    @Test
    void testDeleteSubTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        SubTask subTask = new SubTask(2, "Test SubTask", epic.getId(), duration, startTime);
        epic.addSubTask(subTask);
        epic.deleteSubTask(subTask);
        assertFalse(epic.getSubTasks().contains(subTask));
    }

    @Test
    void testRemoveSubTasksList() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        SubTask subTask1 = new SubTask(2, "Test SubTask1", epic.getId(), duration, startTime);
        SubTask subTask2 = new SubTask(3, "Test SubTask2", epic.getId(), duration, startTime);
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);
        epic.removeSubTasksList();
        assertTrue(epic.getSubTasks().isEmpty());
    }

    @Test
    void testGetType() {
        assertEquals(TaskType.EPIC, epic.getType());
    }

    @Test
    void testGetSubTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        SubTask subTask = new SubTask(2, "Test SubTask", epic.getId(), duration, startTime);
        epic.addSubTask(subTask);
        assertEquals(1, epic.getSubTasks().size());
        assertTrue(epic.getSubTasks().contains(subTask));
    }

    @Test
    void testCalculateStatusWithNoSubTasks() {
        assertEquals(TaskStatus.NEW, epic.calculateStatus());
    }

    @Test
    void testCalculateStatusWithAllDoneSubTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        SubTask subTask1 = new SubTask(2, "Test SubTask1", epic.getId(), duration, startTime);
        subTask1.setStatus(TaskStatus.DONE);
        SubTask subTask2 = new SubTask(3, "Test SubTask2", epic.getId(), duration, startTime);
        subTask2.setStatus(TaskStatus.DONE);
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);
        assertEquals(TaskStatus.DONE, epic.calculateStatus());
    }

    @Test
    void testCalculateStatusWithInProgressSubTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        SubTask subTask1 = new SubTask(2, "Test SubTask1", epic.getId(), duration, startTime);
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        SubTask subTask2 = new SubTask(3, "Test SubTask2", epic.getId(), duration, startTime);
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.calculateStatus());
    }

    @Test
    void testCalculateStatusWithNewSubTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        SubTask subTask1 = new SubTask(2, "Test SubTask1", epic.getId(), duration, startTime);
        SubTask subTask2 = new SubTask(3, "Test SubTask2", epic.getId(), duration, startTime);
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);
        assertEquals(TaskStatus.NEW, epic.calculateStatus());
    }

    @Test
    void testToString() {
        String expectedString = String.format("Эпик='%s', ID=%d, Статус='%s', Начало='%s', Продолжительность='%s'",
                name, id, TaskStatus.NEW, epic.getStartTime(), epic.getDuration());
        assertEquals(expectedString, epic.toString());
    }
}