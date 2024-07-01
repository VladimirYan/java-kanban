package manager;

import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Logger;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static final Logger logger = Logger.getLogger(FileBackedTaskManager.class.getName());
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
        load();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,epicId\n");

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
        String epicId = task instanceof SubTask ? String.valueOf(((SubTask) task).getEpicId()) : "";
        return String.format("%d,%s,%s,%s,%s",
                task.getId(),
                task instanceof Epic ? TaskType.EPIC : task instanceof SubTask ? TaskType.SUBTASK : TaskType.TASK,
                task.getName(),
                task.getStatus(),
                epicId
        );
    }

    public Task taskFromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];

        return switch (type) {
            case TASK -> new Task(id, name);
            case EPIC -> new Epic(id, name);
            case SUBTASK -> {
                int epicId = Integer.parseInt(fields[4]);
                yield new SubTask(id, name, epicId);
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