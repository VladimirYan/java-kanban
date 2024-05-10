package logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import manager.*;
import tasks.*;

class InMemoryHistoryManagerTest {
    @Test
    void shouldAddTaskToHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(1, "Test Task");
        historyManager.add(task);
        assertFalse(historyManager.getHistory().isEmpty());
        assertEquals(task, historyManager.getHistory().getFirst());
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(1, "Test Task");
        historyManager.add(task);
        historyManager.remove(1);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldMaintainHistorySize() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        for (int i = 1; i <= 11; i++) {
            historyManager.add(new Task(i, "Test Task " + i));
        }
        assertEquals(10, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveOldestTaskWhenHistoryExceedsLimit() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        for (int i = 1; i <= 10; i++) {
            historyManager.add(new Task(i, "Test Task " + i));
        }
        Task newTask = new Task(11, "New Task");
        historyManager.add(newTask);
        assertFalse(historyManager.getHistory().contains(new Task(1, "Test Task 1")));
        assertTrue(historyManager.getHistory().contains(newTask));
    }

    @Test
    void shouldThrowExceptionWhenAddingNullTask() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> historyManager.add(null));
        assertEquals("Task cannot be null", exception.getMessage());
    }
}
