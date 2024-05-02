package tasks;

public class Task {
    protected int id;
    protected String name;
    protected TaskStatus status;

    public Task(int id, String name, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
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

    public TaskStatus getStatus() {

        return status;
    }

    public void setStatus(TaskStatus status) {

        this.status = status;
    }

    @Override
    public String toString() {

        return String.format("Название='%s', ID=%d, Статус='%s'", name, id, status);
    }
}
