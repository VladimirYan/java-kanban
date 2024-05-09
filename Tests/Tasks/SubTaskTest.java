package Tasks;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatus;

public class SubTaskTest {
    private SubTask subTask;
    private Epic epic;

    @BeforeEach
    public void setUp() {
        subTask = new SubTask(1, "SubTask Test", 10);
        epic = new Epic(2, "Epic Test");
    }

    @Test
    public void testConstructor() {
        assertEquals(1, subTask.getId());
        assertEquals("SubTask Test", subTask.getName());
        assertEquals(TaskStatus.NEW, subTask.getStatus());
        assertEquals(10, subTask.getEpicId());
    }

    @Test
    public void testGetEpicId() {

        assertEquals(10, subTask.getEpicId());
    }

    @Test
    public void testSetEpic() {
        subTask.setEpic(epic);
        assertEquals(2, subTask.getEpicId());
    }

    //Корректная обработка исключения
    @Test
    public void testSetEpicThrowsException() {
        Epic invalidEpic = new Epic(1, "Invalid Epic");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> subTask.setEpic(invalidEpic));

        assertEquals("A subtask cannot be its own epic", exception.getMessage());
    }

    //Проверка методов, наследованных из родительского класса
    @Test
    public void testInheritedMethods() {
        subTask.setId(3);
        assertEquals(3, subTask.getId());

        subTask.setName("Updated SubTask");
        assertEquals("Updated SubTask", subTask.getName());

        subTask.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, subTask.getStatus());
    }
}
