class SubTask extends Task {
    private final int epicId;

    public SubTask(int id, String name, TaskStatus status, int epicId) {
        super(id, name, status);
        this.epicId = epicId;
    }

    public int getEpicId() {

        return epicId;
    }
}
