package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks = new ArrayList<>();

    public Epic(int id, String name) {
        super(id, name);
        this.status = TaskStatus.NEW;
    }

    public void addSubTask(SubTask subTask) {
        if (subTask.getId() == getId()) {
            throw new IllegalArgumentException("A subtask cannot be its own epic");
        }
        subTasks.add(subTask);
        this.status = calculateStatus();
    }

    //Обновление статуса эпика
    public TaskStatus calculateStatus() {
        if (subTasks.isEmpty()) {
            return TaskStatus.NEW;
        }

        boolean allDone = true;
        boolean anyInProgressOrDone = false;

        for (SubTask subTask : subTasks) {
            TaskStatus status = subTask.getStatus();

            if (status == TaskStatus.IN_PROGRESS || status == TaskStatus.DONE) {
                anyInProgressOrDone = true;
            }
            if (status != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            return TaskStatus.DONE;
        }
        if (anyInProgressOrDone) {
            return TaskStatus.IN_PROGRESS;
        }

        return TaskStatus.NEW;
    }

    public List<SubTask> getSubTasks() {

        return new ArrayList<>(subTasks);
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
        this.status = calculateStatus();
    }
}