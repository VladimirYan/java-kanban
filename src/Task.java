class Task {
    private int id;
    private final String name;
    private TaskStatus status;

    public Task(int id, String name, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public int getId() {

        return id;
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

    public void setId(int id) {

        this.id = id;
    }

    @Override
    public String toString() {
        return " " + name +
                " под номером ID " + id +
                "\nСтатус: " + status;

    }
}
