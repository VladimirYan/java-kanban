package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;
    private final int id = 1;
    private final String name = "Test Task";
    private final Duration duration = Duration.ofHours(1);
    private final LocalDateTime startTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        task = new Task(id, name, duration, startTime);
    }

    @Test
    void testConstructorWithoutDurationAndStartTime() {
        Task newTask = new Task(id, name);
        assertEquals(id, newTask.getId());
        assertEquals(name, newTask.getName());
        assertEquals(TaskStatus.NEW, newTask.getStatus());
        assertNull(newTask.getDuration());
        assertNull(newTask.getStartTime());
    }

    @Test
    void testConstructorWithDurationAndStartTime() {
        Task newTask = new Task(id, name, duration, startTime);
        assertEquals(id, newTask.getId());
        assertEquals(name, newTask.getName());
        assertEquals(TaskStatus.NEW, newTask.getStatus());
        assertEquals(duration, newTask.getDuration());
        assertEquals(startTime, newTask.getStartTime());
    }

    @Test
    void testCopyConstructor() {
        Task copyTask = new Task(task);
        assertEquals(task.getId(), copyTask.getId());
        assertEquals(task.getName(), copyTask.getName());
        assertEquals(task.getStatus(), copyTask.getStatus());
        assertEquals(task.getDuration(), copyTask.getDuration());
        assertEquals(task.getStartTime(), copyTask.getStartTime());
    }

    @Test
    void testGetId() {
        assertEquals(id, task.getId());
    }

    @Test
    void testSetId() {
        int newId = 2;
        task.setId(newId);
        assertEquals(newId, task.getId());
    }

    @Test
    void testGetName() {
        assertEquals(name, task.getName());
    }

    @Test
    void testSetName() {
        String newName = "New Task Name";
        task.setName(newName);
        assertEquals(newName, task.getName());
    }

    @Test
    void testGetStatus() {
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void testSetStatus() {
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;
        task.setStatus(newStatus);
        assertEquals(newStatus, task.getStatus());
    }

    @Test
    void testGetType() {
        assertEquals(TaskType.TASK, task.getType());
    }

    @Test
    void testGetDuration() {
        assertEquals(duration, task.getDuration());
    }

    @Test
    void testSetDuration() {
        Duration newDuration = Duration.ofHours(2);
        task.setDuration(newDuration);
        assertEquals(newDuration, task.getDuration());
    }

    @Test
    void testGetStartTime() {
        assertEquals(startTime, task.getStartTime());
    }

    @Test
    void testSetStartTime() {
        LocalDateTime newStartTime = LocalDateTime.now().plusDays(1);
        task.setStartTime(newStartTime);
        assertEquals(newStartTime, task.getStartTime());
    }

    @Test
    void testGetEndTime() {
        assertEquals(startTime.plus(duration), task.getEndTime());
    }

    @Test
    void testGetEndTimeWithoutStartTime() {
        task.setStartTime(null);
        assertNull(task.getEndTime());
    }

    @Test
    void testGetEndTimeWithoutDuration() {
        task.setDuration(null);
        assertEquals(startTime, task.getEndTime());
    }

    @Test
    void testEquals() {
        Task sameTask = new Task(id, name, duration, startTime);
        assertEquals(task, sameTask);
    }

    @Test
    void testNotEqualsDifferentId() {
        Task differentTask = new Task(2, name, duration, startTime);
        assertNotEquals(task, differentTask);
    }

    @Test
    void testHashCode() {
        Task sameTask = new Task(id, name, duration, startTime);
        assertEquals(task.hashCode(), sameTask.hashCode());
    }

    @Test
    void testToString() {
        String expectedString = String.format("Задача='%s', ID=%d, Статус='%s', Начало='%s', Продолжительность='%s'",
                name, id, TaskStatus.NEW, startTime, duration);
        assertEquals(expectedString, task.toString());
    }
}
