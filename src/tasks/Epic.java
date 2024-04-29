package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Task> subTasks;

    public Epic(int id, String name, TaskStatus status) {
        super(id, name, status);
        this.subTasks = new ArrayList<>();
    }

    public List<Task> getSubTasks() {

        return subTasks;
    }

    public void addSubTask(Task task) {

        subTasks.add(task);
    }
}
