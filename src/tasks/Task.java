package tasks;

import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected TaskStatus status;

    public Task() {
        this.id = 0;
        this.name = "";
        this.status = TaskStatus.NEW;
    }

    public Task(int id, String name) {
        this.id = id;
        this.name = name;
        this.status = TaskStatus.NEW;
    }

    public Task(Task other) {
        this.id = other.id;
        this.name = other.name;
        this.status = other.status;
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
        return String.format("Название='%s', ID=%d, Статус='%s'", name, id, status);
    }
}

