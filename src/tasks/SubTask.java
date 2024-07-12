package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(int id, String name, int epicId, Duration duration, LocalDateTime startTime) {
        super(id, name);
        this.status = TaskStatus.NEW;
        this.epicId = epicId;
        this.duration = duration;
        this.startTime = startTime;
    }

    public SubTask(SubTask other) {
        super(other.getId(), other.getName());
        this.status = other.getStatus();
        this.epicId = other.getEpicId();
        this.duration = other.getDuration();
        this.startTime = other.getStartTime();
    }

    public int getEpicId() {

        return epicId;
    }

    public void setEpic(Epic epic) {
        if (this.id == epic.getId()) {
            throw new IllegalArgumentException("A subtask cannot be its own epic");
        }
        this.epicId = epic.getId();
    }

    @Override
    public TaskType getType() {

        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return String.format("Подзадача='%s', ID=%d, Статус='%s', ЭпикID=%d, Начало='%s', Продолжительность='%s'",
                name, id, status, epicId, startTime, duration);
    }
}
