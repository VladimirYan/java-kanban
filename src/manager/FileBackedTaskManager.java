package manager;

import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static final Logger logger = Logger.getLogger(FileBackedTaskManager.class.getName());
    private final File file;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
        load();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,epicId,startTime,duration\n");

            for (Task task : tasks.values()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (SubTask subTask : subTasks.values()) {
                writer.write(taskToString(subTask) + "\n");
            }
        } catch (IOException e) {
            logger.severe("Ошибка при сохранении задач: " + e.getMessage());
        }
    }

    public String taskToString(Task task) {
        String epicId = task.getType() == TaskType.SUBTASK ? String.valueOf(((SubTask) task).getEpicId()) : "";
        String startTime = task.getStartTime() != null ? task.getStartTime().format(formatter) : "";
        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";

        return String.format("%d,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                epicId,
                startTime,
                duration
        );
    }

    public Task taskFromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);

        LocalDateTime startTime = fields[5].isEmpty() ? null : LocalDateTime.parse(fields[5], formatter);
        Duration duration = fields[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(fields[6]));

        return switch (type) {
            case TASK -> {
                Task task = new Task(id, name);
                task.setStatus(status);
                task.setStartTime(startTime);
                task.setDuration(duration);
                yield task;
            }
            case EPIC -> {
                Epic epic = new Epic(id, name);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                yield epic;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(fields[4]);
                SubTask subTask = new SubTask(id, name, epicId, duration, startTime);
                subTask.setStatus(status);
                yield subTask;
            }
        };
    }

    private void load() {
        if (!file.exists()) {
            return;
        }

        try {
            String fileContent = Files.readString(file.toPath());
            String[] lines = fileContent.split("\n");

            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].trim().isEmpty()) {
                    Task task = taskFromString(lines[i]);
                    if (task instanceof Epic) {
                        epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof SubTask) {
                        subTasks.put(task.getId(), (SubTask) task);
                    } else {
                        tasks.put(task.getId(), task);
                    }
                }
            }
        } catch (IOException e) {
            logger.severe("Ошибка при загрузке задач: " + e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(new InMemoryHistoryManager(), file);
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubTask = super.createSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }
}