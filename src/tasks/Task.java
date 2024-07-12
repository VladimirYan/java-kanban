package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(int id, String name) {
        this.id = id;
        this.name = name;
        this.status = TaskStatus.NEW;
    }

    public Task(int id, String name, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.status = TaskStatus.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(Task other) {
        this.id = other.id;
        this.name = other.name;
        this.status = other.status;
        this.duration = other.duration;
        this.startTime = other.startTime;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public TaskStatus getStatus() {

        return status;
    }

    public void setStatus(TaskStatus status) {

        this.status = status;
    }

    public TaskType getType() {

        return TaskType.TASK;
    }

    public Duration getDuration() {

        return duration;
    }

    public void setDuration(Duration duration) {

        this.duration = duration;
    }

    public LocalDateTime getStartTime() {

        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {

        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        if (duration == null) {
            return startTime;
        }
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Задача='%s', ID=%d, Статус='%s', Начало='%s', Продолжительность='%s'",
                name, id, status, startTime, duration);
    }
}

