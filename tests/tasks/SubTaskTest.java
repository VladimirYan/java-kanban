package tasks;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class SubTaskTest {

    private SubTask subTask;
    private final int id = 1;
    private final String name = "Test SubTask";
    private final int epicId = 10;
    private final Duration duration = Duration.ofHours(1);
    private final LocalDateTime startTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        subTask = new SubTask(id, name, epicId, duration, startTime);
    }

    @Test
    void testConstructor() {
        assertEquals(id, subTask.getId());
        assertEquals(name, subTask.getName());
        assertEquals(TaskStatus.NEW, subTask.getStatus());
        assertEquals(epicId, subTask.getEpicId());
        assertEquals(duration, subTask.getDuration());
        assertEquals(startTime, subTask.getStartTime());
    }

    @Test
    void testCopyConstructor() {
        SubTask copySubTask = new SubTask(subTask);
        assertEquals(subTask.getId(), copySubTask.getId());
        assertEquals(subTask.getName(), copySubTask.getName());
        assertEquals(subTask.getStatus(), copySubTask.getStatus());
        assertEquals(subTask.getEpicId(), copySubTask.getEpicId());
        assertEquals(subTask.getDuration(), copySubTask.getDuration());
        assertEquals(subTask.getStartTime(), copySubTask.getStartTime());
    }

    @Test
    void testSetEpic() {
        Epic epic = new Epic(epicId + 1, "Test Epic");
        subTask.setEpic(epic);
        assertEquals(epic.getId(), subTask.getEpicId());
    }

    @Test
    void testSetEpicWithSameId() {
        Epic epic = new Epic(subTask.getId(), "Test Epic");
        assertThrows(IllegalArgumentException.class, () -> subTask.setEpic(epic));
    }

    @Test
    void testGetType() {
        assertEquals(TaskType.SUBTASK, subTask.getType());
    }

    @Test
    void testToString() {
        String expectedString = String.format("Подзадача='%s', ID=%d, Статус='%s', ЭпикID=%d, Начало='%s', Продолжительность='%s'",
                name, id, TaskStatus.NEW, epicId, startTime, duration);
        assertEquals(expectedString, subTask.toString());
    }


}
