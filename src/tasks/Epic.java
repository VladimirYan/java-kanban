package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasks = new ArrayList<>();

    public Epic(int id, String name, TaskStatus status) {
        super(id, name, status);
    }

    //Метод автоматически обновляет статус эпика в зависимости от статусов подзадач
    public TaskStatus calculateStatus() {
        boolean allDone = subTasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);
        boolean anyInProgress = subTasks.stream().anyMatch(subtask -> subtask.getStatus() == TaskStatus.IN_PROGRESS);
        boolean anyNotNew = subTasks.stream().anyMatch(subtask -> subtask.getStatus() != TaskStatus.NEW);

        if (allDone) return TaskStatus.DONE;
        if (anyInProgress) return TaskStatus.IN_PROGRESS;
        if (anyNotNew) return TaskStatus.IN_PROGRESS;
        return TaskStatus.NEW;
    }

    public void addSubtask(SubTask subtask) {
        subTasks.add(subtask);
        this.status = calculateStatus();
    }

    public List<SubTask> getSubTasks() {

        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
        this.status = calculateStatus();
    }
}