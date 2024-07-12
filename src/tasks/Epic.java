package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.Duration;

public class Epic extends Task {
    private final List<SubTask> subTasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(int id, String name) {
        super(id, name);
        this.status = calculateStatus();
    }

    @Override
    public LocalDateTime getEndTime() {

        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {

        this.endTime = endTime;
    }

    public void addSubTask(SubTask subTask) {
        if (subTask.getId() == getId()) {
            throw new IllegalArgumentException("Подзадача не может быть своим собственным эпиком.");
        }
        subTasks.add(subTask);
    }

    public void deleteSubTask(SubTask subTask) {

        subTasks.removeIf(s -> s.getId() == subTask.getId());
    }

    public void removeSubTasksList() {

        subTasks.clear();
    }

    @Override
    public TaskType getType() {

        return TaskType.EPIC;
    }

    public List<SubTask> getSubTasks() {

        return new ArrayList<>(subTasks);
    }

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

    public void createEpicDateTime() {
        if (subTasks.isEmpty()) {
            resetTiming();
            return;
        }

        Optional<LocalDateTime> minStartTime = subTasks.stream()
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo);
        Optional<LocalDateTime> maxEndTime = subTasks.stream()
                .map(SubTask::getEndTime)
                .max(LocalDateTime::compareTo);
        Optional<Duration> totalDuration = subTasks.stream()
                .map(SubTask::getDuration)
                .reduce(Duration::plus);

        setStartTime(minStartTime.orElse(null));
        setEndTime(maxEndTime.orElse(null));
        setDuration(totalDuration.orElse(null));
    }

    private void resetTiming() {
        setStartTime(null);
        setEndTime(null);
        setDuration(null);
    }

    @Override
    public String toString() {
        return String.format("Эпик='%s', ID=%d, Статус='%s', Начало='%s', Продолжительность='%s'",
                name, id, status, startTime, duration);
    }
}