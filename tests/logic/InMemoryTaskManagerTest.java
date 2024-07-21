package logic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import manager.*;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    void generateIdShouldReturnIncrementingIds() {
        int firstId = taskManager.generateId();
        int secondId = taskManager.generateId();
        assertEquals(firstId + 1, secondId, "Метод generateId должен возвращать инкрементируемые ID.");
    }

    @Test
    void getHistoryShouldReturnEmptyListIfNoHistory() {
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "Метод getHistory должен возвращать пустой список, если история пуста.");
    }

    @Test
    void getAllTasksShouldReturnAllTasks() {
        Task task = new Task(0, "Test Task");
        taskManager.createTask(task);
        List<Task> tasks = taskManager.getAllTasks();
        assertFalse(tasks.isEmpty(), "Метод getAllTasks должен возвращать все задачи.");
        assertEquals(1, tasks.size(), "Метод getAllTasks должен возвращать список с одной задачей.");
    }

    @Test
    void getAllEpicsShouldReturnAllEpics() {
        Epic epic = new Epic(0, "Test Epic");
        taskManager.createEpic(epic);
        List<Epic> epics = taskManager.getAllEpics();
        assertFalse(epics.isEmpty(), "Метод getAllEpics должен возвращать все эпики.");
        assertEquals(1, epics.size(), "Метод getAllEpics должен возвращать список с одним эпиком.");
    }

    @Test
    void getAllSubTasksShouldReturnAllSubTasks() {
        Epic epic = new Epic(0, "Test Epic");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask(0, "Test SubTask", epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        taskManager.createSubTask(subTask);
        List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertFalse(subTasks.isEmpty(), "Метод getAllSubTasks должен возвращать все подзадачи.");
        assertEquals(1, subTasks.size(), "Метод getAllSubTasks должен возвращать список с одной подзадачей.");
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        Task task = new Task(0, "Test Task", Duration.ofHours(1), LocalDateTime.now());
        Task createdTask = taskManager.createTask(task);
        assertNotNull(createdTask, "Созданная задача не должна быть null");
        assertEquals("Test Task", createdTask.getName(), "Имена задач должны совпадать");
    }

    @Test
    void shouldNotCreateTaskWithExistingId() {
        Task task1 = new Task(1, "Test Task 1");
        taskManager.createTask(task1);
        Task task2 = new Task(1, "Test Task 2");
        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2),
                "Должно возникнуть исключение, если задача с таким ID уже существует");
    }

    @Test
    void shouldCreateEpicSuccessfully() {
        Epic epic = new Epic(0, "Test Epic");
        Epic createdEpic = taskManager.createEpic(epic);
        assertNotNull(createdEpic, "Созданный эпик не должен быть null");
        assertEquals("Test Epic", createdEpic.getName(), "Имена эпиков должны совпадать");
    }

    @Test
    void shouldNotCreateEpicWithExistingId() {
        Epic epic1 = new Epic(1, "Test Epic 1");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic(1, "Test Epic 2");
        assertThrows(IllegalArgumentException.class, () -> taskManager.createEpic(epic2),
                "Должно возникнуть исключение, если эпик с таким ID уже существует");
    }

    @Test
    void shouldCreateSubTaskSuccessfully() {
        Epic epic = new Epic(0, "Test Epic");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask(0, "Test SubTask", epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        SubTask createdSubTask = taskManager.createSubTask(subTask);
        assertNotNull(createdSubTask, "Созданная подзадача не должна быть null");
        assertEquals("Test SubTask", createdSubTask.getName(), "Имена подзадач должны совпадать");
    }

    @Test
    void shouldNotCreateSubTaskWithExistingId() {
        Epic epic = new Epic(0, "Test Epic");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask(1, "Test SubTask 1", epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask(1, "Test SubTask 2", epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        taskManager.createSubTask(subTask2);
        assertThrows(IllegalArgumentException.class, () -> taskManager.createSubTask(subTask2),
                "Должно возникнуть исключение, если подзадача с таким ID уже существует");
    }

    @Test
    void shouldReturnTaskById() {
        Task task = new Task(0, "Test Task");
        taskManager.createTask(task);
        Task foundTask = taskManager.getTask(task.getId());
        assertNotNull(foundTask, "Метод getTask должен возвращать задачу по ID");
        assertEquals(task.getName(), foundTask.getName(), "Названия задач должны совпадать");
    }

    @Test
    void shouldReturnNullIfTaskNotFound() {
        assertNull(taskManager.getTask(1), "Метод getTask должен возвращать null, если задача не найдена");
    }

    @Test
    void testGetTaskAddsToHistory() {
        Task task = new Task(1, "Тестовая задача", Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task);

        taskManager.getTask(task.getId());

        assertTrue(taskManager.getHistory().contains(task), "Ожидается, что задача будет добавлена в историю");
    }

    @Test
    void shouldReturnEpicById() {
        Epic epic = new Epic(0, "Test Epic");
        taskManager.createEpic(epic);
        Epic foundEpic = taskManager.getEpic(epic.getId());
        assertNotNull(foundEpic, "Метод getEpic должен возвращать эпик по ID");
        assertEquals(epic.getName(), foundEpic.getName(), "Названия эпиков должны совпадать");
    }

    @Test
    void shouldReturnNullIfEpicNotFound() {
        assertNull(taskManager.getEpic(1), "Метод getEpic должен возвращать null, если эпик не найден");
    }

    @Test
    void testGetEpicAddsToHistory() {
        Epic epic = new Epic(1, "Тестовый Эпик");
        taskManager.createEpic(epic);

        taskManager.getEpic(epic.getId());

        assertTrue(taskManager.getHistory().contains(epic), "Ожидается, что эпик будет добавлен в историю");
    }


    @Test
    void testGetSubTaskReturnsSubTaskIfExists() {
        Epic epic = new Epic(1, "Тестовый Эпик");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask(2, "Тестовая Подзадача", epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        taskManager.createSubTask(subTask);

        SubTask fetchedSubTask = taskManager.getSubTask(subTask.getId());
        assertNotNull(fetchedSubTask, "Ожидается, что подзадача будет возвращена");
        assertEquals(subTask.getId(), fetchedSubTask.getId(), "Ожидается, что ID подзадачи будет совпадать");
        assertEquals(subTask.getName(), fetchedSubTask.getName(), "Ожидается, что имя подзадачи будет совпадать");
    }

    @Test
    void shouldReturnNullIfSubTaskNotFound() {
        assertNull(taskManager.getSubTask(1), "Метод getSubTask должен возвращать null, если подзадача не найдена");
    }

    @Test
    void testGetSubTaskAddsToHistory() {
        Epic epic = new Epic(1, "Тестовый Эпик");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask(2, "Тестовая Подзадача", epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        taskManager.createSubTask(subTask);

        taskManager.getSubTask(subTask.getId());

        assertTrue(taskManager.getHistory().contains(subTask), "Ожидается, что подзадача будет добавлена в историю");
    }


    @Test
    void shouldUpdateTaskSuccessfully() {
        Task originalTask = new Task(1, "Original Task");
        taskManager.createTask(originalTask);
        Task updatedTask = new Task(1, "Updated Task");
        taskManager.updateTask(updatedTask);
        Task retrievedTask = taskManager.getTask(1);
        assertEquals("Updated Task", retrievedTask.getName(), "Имя задачи должно быть обновлено");
    }

    @Test
    void shouldThrowExceptionIfTaskDoesNotExist() {
        Task task = new Task(99, "Non-existent Task");
        assertThrows(IllegalArgumentException.class, () -> taskManager.updateTask(task), "Должно возникнуть исключение, если задача не существует");
    }

    @Test
    void shouldUpdateEpicSuccessfully() {
        Epic originalEpic = new Epic(1, "Original Epic");
        taskManager.createEpic(originalEpic);
        Epic updatedEpic = new Epic(1, "Updated Epic");
        taskManager.updateEpic(updatedEpic);
        Epic retrievedEpic = taskManager.getEpic(1);
        assertEquals("Updated Epic", retrievedEpic.getName(), "Имя эпика должно быть обновлено");
    }

    @Test
    void shouldThrowExceptionIfEpicDoesNotExist() {
        Epic epic = new Epic(99, "Non-existent Epic");
        assertThrows(IllegalArgumentException.class, () -> taskManager.updateEpic(epic), "Должно возникнуть исключение, если эпик не существует");
    }

    @Test
    void shouldUpdateSubTaskSuccessfully() {
        Epic epic = new Epic(1, "Test Epic");
        taskManager.createEpic(epic);
        LocalDateTime startTime = LocalDateTime.now();
        SubTask originalSubTask = new SubTask(1, "Original SubTask", 1, Duration.ofHours(1), startTime);
        taskManager.createSubTask(originalSubTask);
        SubTask updatedSubTask = new SubTask(2, "Updated SubTask", 1, Duration.ofHours(2), startTime.plusHours(1));
        taskManager.updateSubTask(updatedSubTask);
        assertEquals("Updated SubTask", updatedSubTask.getName(), "Имя подзадачи должно быть обновлено");
        assertEquals(Duration.ofHours(2), updatedSubTask.getDuration(), "Длительность подзадачи должна быть обновлена");
        assertEquals(startTime.plusHours(1), updatedSubTask.getStartTime(), "Время начала подзадачи должно быть обновлено");
    }

    @Test
    void shouldThrowExceptionIfSubTaskDoesNotExist() {
        LocalDateTime startTime = LocalDateTime.now();
        SubTask subTask = new SubTask(99, "Non-existent SubTask", 1, Duration.ofHours(1), startTime);
        assertThrows(IllegalArgumentException.class, () -> taskManager.updateSubTask(subTask),
                "Должно возникнуть исключение, если подзадача не существует");
    }

    @Test
    void shouldDeleteTaskByIdSuccessfully() {
        Task task = new Task(1, "Test Task");
        taskManager.createTask(task);
        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTask(1), "Задача должна быть удалена");
    }

    @Test
    void shouldThrowExceptionIfTaskIdDoesNotExistWhenDeleting() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.deleteTaskById(99), "Должно возникнуть исключение, если задача не существует");
    }

    @Test
    void shouldDeleteEpicByIdSuccessfully() {
        Epic epic = new Epic(1, "Test Epic");
        taskManager.createEpic(epic);
        taskManager.deleteEpicById(1);
        assertNull(taskManager.getEpic(1), "Эпик должен быть удален");
    }

    @Test
    void shouldThrowExceptionIfEpicIdDoesNotExistWhenDeleting() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.deleteEpicById(99), "Должно возникнуть исключение, если эпик не существует");
    }

    @Test
    void shouldDeleteSubTaskByIdSuccessfully() {
        Epic epic = new Epic(1, "Test Epic");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask(2, "Test SubTask", 1, Duration.ZERO, LocalDateTime.now());
        taskManager.createSubTask(subTask);
        taskManager.deleteSubTaskById(2);
        assertNull(taskManager.getSubTask(1), "Подзадача должна быть удалена");
    }

    @Test
    void shouldThrowExceptionIfSubTaskIdDoesNotExistWhenDeleting() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.deleteSubTaskById(99), "Должно возникнуть исключение, если подзадача не существует");
    }

    @Test
    void shouldDeleteAllTasksSuccessfully() {
        Task task1 = new Task(1, "Test Task 1");
        Task task2 = new Task(2, "Test Task 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны быть удалены");
    }

    @Test
    void shouldDeleteAllEpicsAndSubTasksSuccessfully() {
        Epic epic1 = new Epic(1, "Test Epic 1");
        Epic epic2 = new Epic(2, "Test Epic 2");
        SubTask subTask1 = new SubTask(1, "Test SubTask 1", 1, Duration.ofHours(1), LocalDateTime.now());
        SubTask subTask2 = new SubTask(2, "Test SubTask 2", 1, Duration.ofHours(2), LocalDateTime.now().plusHours(2));
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty(), "Все эпики должны быть удалены");
        assertTrue(taskManager.getAllSubTasks().isEmpty(), "Все подзадачи должны быть удалены");
    }

    @Test
    void shouldDeleteAllSubTasksOnlySuccessfully() {
        Epic epic = new Epic(1, "Test Epic");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask(2, "Test SubTask 1", 1, Duration.ofHours(1), LocalDateTime.now());
        SubTask subTask2 = new SubTask(3, "Test SubTask 2", 1, Duration.ofHours(2), LocalDateTime.now().plusHours(3));
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.deleteAllSubTasks();
        assertTrue(taskManager.getAllSubTasks().isEmpty(), "Все подзадачи должны быть удалены");
        assertFalse(taskManager.getAllEpics().isEmpty(), "Эпики не должны быть удалены");
    }

    @Test
    void shouldReturnAllSubTasksOfAnEpic() {
        Epic epic = new Epic(1, "Test Epic");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask(2, "Test SubTask 1", 1, Duration.ofHours(1), LocalDateTime.now());
        SubTask subTask2 = new SubTask(3, "Test SubTask 2", 1, Duration.ofHours(2), LocalDateTime.now().plusHours(4));
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        List<SubTask> subTasksOfEpic = taskManager.getEpicSubtasks(1);
        assertEquals(2, subTasksOfEpic.size(), "Количество подзадач эпика должно соответствовать добавленным");
        assertTrue(subTasksOfEpic.contains(subTask1) && subTasksOfEpic.contains(subTask2), "Список подзадач должен содержать добавленные подзадачи");
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        Task task1 = new Task(1, "Test Task 1");
        Task task2 = new Task(2, "Test Task 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size(), "Количество приоритетных задач должно соответствовать добавленным");
        assertTrue(prioritizedTasks.contains(task1) && prioritizedTasks.contains(task2), "Список приоритетных задач должен содержать добавленные задачи");
    }

    @Test
    void shouldReturnFalseWhenNoOverlappingTasks() {
        Task task1 = new Task(1, "Test Task 1", Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task(2, "Test Task 2", Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        assertFalse(taskManager.isTaskInvalid(task2), "Задача не должна пересекаться по времени с существующими задачами");
    }

    @Test
    void shouldReturnTrueWhenTasksOverlap() {
        Task task1 = new Task(1, "Test Task 1", Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task(2, "Test Task 2", Duration.ofHours(1), LocalDateTime.now().plusMinutes(30));
        assertTrue(taskManager.isTaskInvalid(task2), "Задача должна пересекаться по времени с существующими задачами");
    }

    @Test
    void shouldReturnTrueWhenTaskStartsAtTheEndOfAnother() {
        Task task1 = new Task(1, "Test Task 1", Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task(2, "Test Task 2", Duration.ofHours(1), task1.getEndTime());
        assertTrue(taskManager.isTaskInvalid(task2), "Задача должна считаться пересекающейся, если начинается в момент окончания другой задачи");
    }

    @Test
    void shouldReturnTrueWhenTaskEndsAtTheStartOfAnother() {
        Task task1 = new Task(1, "Test Task 1", Duration.ofHours(1), LocalDateTime.now().plusHours(1));
        taskManager.createTask(task1);
        Task task2 = new Task(2, "Test Task 2", Duration.ofHours(1), LocalDateTime.now());
        assertTrue(taskManager.isTaskInvalid(task2), "Задача должна считаться пересекающейся, если заканчивается в момент начала другой задачи");
    }
}



