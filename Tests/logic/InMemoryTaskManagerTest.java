package logic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import manager.*;
import tasks.*;

import java.util.List;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    //Создание задач разного типа (Task/Epic/SubTask)
    //Поиск задачи по ID
    @Test
    void shouldAddAndFindTasksById() {
        Task task = new Task(0, "Test Task");
        Epic epic = new Epic(0, "Test Epic");
        SubTask subTask = new SubTask(0, "Test SubTask", 1);

        Task addedTask = taskManager.createTask(task);
        Epic addedEpic = taskManager.createEpic(epic);
        SubTask addedSubTask = taskManager.createSubTask(subTask);

        assertEquals(addedTask, taskManager.getTask(addedTask.getId()));
        assertEquals(addedEpic, taskManager.getEpic(addedEpic.getId()));
        assertEquals(addedSubTask, taskManager.getSubTask(addedSubTask.getId()));
    }

    //Обработка исключений при попытке создать задачу с одинаковым ID
    @Test
    void shouldNotConflictTasksWithSameId() {
        Task task1 = new Task(1, "Test Task 1");
        Task task2 = new Task(1, "Test Task 2");

        taskManager.createTask(task1);
        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void shouldGenerateUniqueId() {
        Task task1 = taskManager.createTask(new Task(0,"Task 1"));
        Task task2 = taskManager.createTask(new Task(0,"Task 2"));
        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    public void historyManagerPreservesTaskVersions() {
        Task task = new Task(1, "Initial Task");
        Task updatedTask = new Task(1, "Updated Task");

        taskManager.createTask(task);
        taskManager.updateTask(updatedTask);
        taskManager.getTask(1);

        Task firstVersion = taskManager.getHistory().get(0);
        Task secondVersion = taskManager.getHistory().get(1);

        assertNotEquals(firstVersion.getName(), secondVersion.getName());
    }

    //Неизменность задачи при ее добавлении в менеджер
    @Test
    public void testTaskImmutabilityAfterAddition() {
        Task task = new Task(1, "Initial Task");
        taskManager.createTask(task);

        Task fetchedTask = taskManager.getTask(1);
        fetchedTask.setName("Modified Task");

        Task refetchedTask = taskManager.getTask(1);
        assertEquals("Initial Task", refetchedTask.getName());
        assertEquals(TaskStatus.NEW, refetchedTask.getStatus());
    }

    @Test
    void shouldAddAndRetrieveTask() {
        Task task = new Task(0, "Test Task");
        Task addedTask = taskManager.createTask(task);
        Task retrievedTask = taskManager.getTask(addedTask.getId());
        assertNotNull(retrievedTask);
        assertEquals("Test Task", retrievedTask.getName());
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        Epic epic = new Epic(0, "Test Epic");
        Epic addedEpic = taskManager.createEpic(epic);
        Epic retrievedEpic = taskManager.getEpic(addedEpic.getId());
        assertNotNull(retrievedEpic);
        assertEquals("Test Epic", retrievedEpic.getName());
    }

    @Test
    void shouldAddAndRetrieveSubTask() {
        Epic epic = new Epic(0, "Test Epic");
        Epic addedEpic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(0, "Test SubTask", addedEpic.getId());
        SubTask addedSubTask = taskManager.createSubTask(subTask);
        SubTask retrievedSubTask = taskManager.getSubTask(addedSubTask.getId());
        assertNotNull(retrievedSubTask);
        assertEquals("Test SubTask", retrievedSubTask.getName());
        assertEquals(addedEpic.getId(), retrievedSubTask.getEpicId());
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task(0, "Old Task");
        Task addedTask = taskManager.createTask(task);
        Task updatedTask = new Task(addedTask.getId(), "Updated Task");
        taskManager.updateTask(updatedTask);
        Task retrievedTask = taskManager.getTask(addedTask.getId());
        assertEquals("Updated Task", retrievedTask.getName());
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic = new Epic(0, "Original Epic");
        Epic addedEpic = taskManager.createEpic(epic);
        Epic updatedEpic = new Epic(addedEpic.getId(), "Updated Epic");
        taskManager.updateEpic(updatedEpic);
        Epic retrievedEpic = taskManager.getEpic(updatedEpic.getId());
        assertNotNull(retrievedEpic);
        assertEquals("Updated Epic", retrievedEpic.getName());
    }

    @Test
    void shouldUpdateSubTask() {
        Epic epic = new Epic(0, "Epic for SubTask");
        Epic addedEpic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(0, "Original SubTask", addedEpic.getId());
        SubTask addedSubTask = taskManager.createSubTask(subTask);
        SubTask updatedSubTask = new SubTask(addedSubTask.getId(), "Updated SubTask", addedEpic.getId());
        taskManager.updateSubTask(updatedSubTask);
        SubTask retrievedSubTask = taskManager.getSubTask(updatedSubTask.getId());
        assertNotNull(retrievedSubTask);
        assertEquals("Updated SubTask", retrievedSubTask.getName());
        assertEquals(addedEpic.getId(), retrievedSubTask.getEpicId());
    }

    @Test
    void shouldDeleteTaskByIdAndFromHistory() {
        Task task = new Task(0, "Task for Deletion");
        Task addedTask = taskManager.createTask(task);
        taskManager.getTask(addedTask.getId());
        taskManager.deleteTaskById(addedTask.getId());
        assertNull(taskManager.getTask(addedTask.getId()));
        assertFalse(taskManager.getHistory().contains(addedTask));
    }

    //тесты для deleteEpicById(int id) и deleteSubTaskById(int id)
    //тесты для deleteAll(Tasks, Epics, SubTasks)
    //прочие тесты (для обработки конкретных сценариев)

    @Test
    void getEpicSubtasks_ShouldReturnListOfSubTasksForEpic() {
        Epic epic = new Epic(1, "Test Epic");
        SubTask subTask1 = new SubTask(2, "Test SubTask 1", 1);
        SubTask subTask2 = new SubTask(3, "Test SubTask 2", 1);
        SubTask subTask3 = new SubTask(4, "Test SubTask 3", 2);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        List<SubTask> subTasksForEpic = taskManager.getEpicSubtasks(1);

        assertTrue(subTasksForEpic.contains(subTask1));
        assertTrue(subTasksForEpic.contains(subTask2));
        assertFalse(subTasksForEpic.contains(subTask3));

        assertEquals(2, subTasksForEpic.size());
    }
}

