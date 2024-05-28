package logic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import manager.*;
import tasks.*;

import java.util.List;

class InMemoryTaskManagerTest {
     InMemoryTaskManager taskManager;

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
        SubTask subTask = new SubTask(0, "Test SubTask", epic.getId());

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
    public void testGenerateId() {
        int firstId = taskManager.generateId();
        int secondId = taskManager.generateId();
        assertNotEquals(firstId, secondId);
    }

    @Test
    public void testGetAllTasks() {
        Task task1 = new Task(1, "Task 1");
        Task task2 = new Task(2, "Task 2");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test
    public void testGetAllEpics() {
        Epic epic1 = new Epic(1, "Epic 1");
        Epic epic2 = new Epic(2, "Epic 2");

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        List<Epic> epics = taskManager.getAllEpics();

        assertEquals(2, epics.size());
        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
    }

    @Test
    public void testGetAllSubTasks() {
        Epic epic = new Epic(1, "Epic 1");
        SubTask subTask1 = new SubTask(2, "SubTask 1", epic.getId());
        SubTask subTask2 = new SubTask(3, "SubTask 2", epic.getId());

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertEquals(2, subTasks.size());
        assertTrue(subTasks.contains(subTask1));
        assertTrue(subTasks.contains(subTask2));
    }

    @Test
    public void testCreateTask() {
        Task task = new Task(0, "Task 1");

        Task createdTask = taskManager.createTask(task);

        assertNotNull(createdTask);
        assertEquals("Task 1", createdTask.getName());
        assertTrue(createdTask.getId() > 0);
    }

    @Test
    public void testCreateEpic() {
        Epic epic = new Epic(0, "Epic 1");

        Epic createdEpic = taskManager.createEpic(epic);

        assertNotNull(createdEpic);
        assertEquals("Epic 1", createdEpic.getName());
        assertTrue(createdEpic.getId() > 0);
    }

    @Test
    public void testCreateSubTask() {
        // Создаём Epic
        Epic epic = new Epic(0, "Epic 1");
        Epic createdEpic = taskManager.createEpic(epic);

        // Создаём SubTask
        SubTask subTask = new SubTask(0, "SubTask 1", createdEpic.getId());
        SubTask createdSubTask = taskManager.createSubTask(subTask);

        // Проверки на существование подзадачи и корректность данных
        assertNotNull(createdSubTask);
        assertEquals("SubTask 1", createdSubTask.getName());
        assertTrue(createdSubTask.getId() > 0);
        assertEquals(createdEpic.getId(), createdSubTask.getEpicId());

        // Проверяем, что SubTask добавлена в Epic
        Epic updatedEpic = taskManager.getEpic(createdEpic.getId());
        assertNotNull(updatedEpic);
    }

    @Test
    public void testGetTask() {
        Task task = new Task(1, "Task 1");
        taskManager.createTask(task);
        Task returnedTask = taskManager.getTask(task.getId());

        assertNotNull(returnedTask);
        assertEquals(task.getId(), returnedTask.getId());
        assertEquals(task.getName(), returnedTask.getName());
        assertEquals(task.getStatus(), returnedTask.getStatus());

        // Проверка истории
        assertTrue(taskManager.getHistory().contains(returnedTask),
                "Task должен быть в истории после выборки");
    }

    @Test
    public void testGetEpic() {
        Epic epic = new Epic(1, "Epic 1");
        taskManager.createEpic(epic);
        Epic returnedEpic = taskManager.getEpic(epic.getId());

        assertNotNull(returnedEpic);
        assertEquals(epic.getId(), returnedEpic.getId());
        assertEquals(epic.getName(), returnedEpic.getName());
        assertEquals(epic.getStatus(), returnedEpic.getStatus());

        // Проверка истории
        assertTrue(taskManager.getHistory().contains(returnedEpic),
                "Epic должен быть в истории после выборки");
    }

    @Test
    public void testGetSubTask() {
        Epic epic = new Epic(1, "Epic 1");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask(2, "SubTask 1", epic.getId());
        taskManager.createSubTask(subTask);
        SubTask returnedSubTask = taskManager.getSubTask(subTask.getId());

        assertNotNull(returnedSubTask);
        assertEquals(subTask.getId(), returnedSubTask.getId());
        assertEquals(subTask.getName(), returnedSubTask.getName());
        assertEquals(subTask.getStatus(), returnedSubTask.getStatus());
        assertEquals(subTask.getEpicId(), returnedSubTask.getEpicId());

        // Проверка истории
        assertTrue(taskManager.getHistory().contains(returnedSubTask),
                "SubTask должен быть в истории после выборки");
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task(0, "Old Task");
        Task addedTask = taskManager.createTask(task);
        Task updatedTask = new Task(addedTask.getId(), "Updated Task");
        taskManager.updateTask(updatedTask);
        Task retrievedTask = taskManager.getTask(addedTask.getId());
        assertEquals("Updated Task", retrievedTask.getName());
        assertTrue(taskManager.getHistory().contains(updatedTask));
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
        assertTrue(taskManager.getHistory().contains(updatedEpic));
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
        assertTrue(taskManager.getHistory().contains(updatedSubTask));
    }

    @Test
    void shouldAddAndRetrieveTask() {
        Task task = new Task(0, "Test Task");
        Task addedTask = taskManager.createTask(task);
        Task retrievedTask = taskManager.getTask(addedTask.getId());
        assertNotNull(retrievedTask);
        assertEquals("Test Task", retrievedTask.getName());
        assertTrue(taskManager.getHistory().contains(addedTask));
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        Epic epic = new Epic(0, "Test Epic");
        Epic addedEpic = taskManager.createEpic(epic);
        Epic retrievedEpic = taskManager.getEpic(addedEpic.getId());
        assertNotNull(retrievedEpic);
        assertEquals("Test Epic", retrievedEpic.getName());
        assertTrue(taskManager.getHistory().contains(addedEpic));
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
        assertTrue(taskManager.getHistory().contains(addedSubTask));
    }

    @Test
    void shouldDeleteTaskById() {
        Task task = new Task(0, "Test Task");
        Task addedTask = taskManager.createTask(task);
        assertNotNull(taskManager.getTask(addedTask.getId()));

        taskManager.deleteTaskById(addedTask.getId());
        assertNull(taskManager.getTask(addedTask.getId()));

        // Проверка удаления из истории
        assertFalse(taskManager.getHistory().contains(addedTask));
    }

    //Тест для deleteEpicById(int id)

    @Test
    void shouldDeleteSubTaskById() {
        Epic epic = new Epic(0, "Test Epic");
        Epic addedEpic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(0, "Test SubTask", addedEpic.getId());
        SubTask addedSubTask = taskManager.createSubTask(subTask);

        assertNotNull(taskManager.getSubTask(addedSubTask.getId()));

        taskManager.deleteSubTaskById(addedSubTask.getId());
        assertNull(taskManager.getSubTask(addedSubTask.getId()));

        // Проверка удаления из истории
        assertFalse(taskManager.getHistory().contains(addedSubTask));
    }
}

