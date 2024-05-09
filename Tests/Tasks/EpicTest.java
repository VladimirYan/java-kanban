package Tasks;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    void testAddSubTask() {
        Epic epic = new Epic(1, "Epic Task");
        SubTask subTask1 = new SubTask(2, "Sub Task 1", 1);
        SubTask subTask2 = new SubTask(3, "Sub Task 2", 1);
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.NEW);

        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);

        assertEquals(2, epic.getSubTasks().size());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testCalculateStatus() {
        Epic epic = new Epic(1, "Epic Task");
        SubTask subTask1 = new SubTask(2, "Sub Task 1", 1);
        SubTask subTask2 = new SubTask(3, "Sub Task 2", 1);

        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);

        assertEquals(TaskStatus.DONE, epic.calculateStatus());
    }

    @Test
    void testRemoveSubTask() {
        Epic epic = new Epic(1, "Epic Task");
        SubTask subTask1 = new SubTask(2, "Sub Task 1", 1);
        epic.addSubTask(subTask1);
        assertEquals(1, epic.getSubTasks().size());

        epic.removeSubTask(subTask1);
        assertEquals(0, epic.getSubTasks().size());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }
}