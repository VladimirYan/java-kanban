package Tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.SubTask;
import tasks.Epic;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;

    @BeforeEach
    public void setUp() {

        task = new Task(1, "Test task");
    }

    @Test
    public void testConstructor() {
        assertEquals(1, task.getId());
        assertEquals("Test task", task.getName());
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    public void testSettersAndGetters() {
        task.setId(2);
        assertEquals(2, task.getId());

        task.setName("Updated task");
        assertEquals("Updated task", task.getName());

        task.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    public void testEquals() {
        Task anotherTask = new Task(1, "Another task");
        Task differentTask = new Task(2, "Different task");

        assertEquals(task, anotherTask);
        assertNotEquals(task, differentTask);
        assertNotEquals(null, task);
        assertNotEquals(task, new Object());
    }

    @Test
    public void testHashCode() {
        Task anotherTask = new Task(1, "Another task");
        assertEquals(task.hashCode(), anotherTask.hashCode());

        anotherTask.setId(3);
        assertNotEquals(task.hashCode(), anotherTask.hashCode());
    }

    @Test
    public void testToString() {
        String expectedString = "Название='Test task', ID=1, Статус='NEW'";
        assertEquals(expectedString, task.toString());
    }

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task(1, "Task 1");
        Task task2 = new Task(1, "Task 2");

        assertEquals(task1, task2, "Tasks should be equal as their IDs are equal");
    }

    @Test
    void testSubtaskEqualityById() {
        SubTask subtask1 = new SubTask(1, "Subtask 1", 2);
        SubTask subtask2 = new SubTask(1, "Subtask 2", 3);

        assertEquals(subtask1, subtask2, "Subtasks should be equal as their IDs are equal");
    }

    @Test
    void epicCannotContainItselfAsSubTask() {
        Epic epic1 = new Epic(1, "Epic 1");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> epic1.addSubTask(new SubTask(1, "Subtask 1", 1)));

        assertTrue(exception.getMessage().contains("A subtask cannot be its own epic"), "Epic should not contain itself as a subtask");
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        SubTask subtask = new SubTask(1, "Subtask", 0);
        Epic epic = new Epic(1, "Epic");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> subtask.setEpic(epic));

        assertTrue(exception.getMessage().contains("A subtask cannot be its own epic"), "Subtask should not be able to set itself as its own epic");
    }
}
