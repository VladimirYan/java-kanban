package manager;

import tasks.Task;
import java.util.LinkedList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new LinkedList<>();

    @Override
    public List<Task> getHistory() {

        return new LinkedList<>(history);
    }

    @Override
    public void add(Task task) {
        if (history.size() == 10) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public void remove(int id) {

        history.removeIf(t -> t.getId() == id);
    }
}
