package Logic;

import org.junit.jupiter.api.Test;

import manager.TaskManager;
import manager.HistoryManager;
import manager.Managers;
import manager.InMemoryTaskManager;
import manager.InMemoryHistoryManager;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    public void testGetDefaultInMemoryManager_isNotNull_andCorrectType() {
        TaskManager manager = Managers.getDefaultInMemoryManager();
        assertNotNull(manager, "TaskManager should not be null");
        assertInstanceOf(InMemoryTaskManager.class, manager,
                "Should return instance of InMemoryTaskManager");
    }

    @Test
    public void testGetDefaultHistory_isNotNull_andCorrectType() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager should not be null");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager,
                "Should return instance of InMemoryHistoryManager");
    }
}
