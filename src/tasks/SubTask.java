package tasks;

public class SubTask extends Task {
    private int epicId;

    public SubTask() {
        super();
        this.status = TaskStatus.NEW;
    }

    public SubTask(int id, String name, int epicId) {
        super(id, name);
        this.status = TaskStatus.NEW;
        this.epicId = epicId;
    }

    public SubTask(SubTask other) {
        super(other.getId(), other.getName());
        this.status = other.getStatus();
        this.epicId = other.getEpicId();
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
}
