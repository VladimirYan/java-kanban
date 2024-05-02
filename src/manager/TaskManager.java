package manager;

import tasks.SubTask;
import tasks.Task;
import tasks.Epic;
import tasks.TaskStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subtasks = new HashMap<>();

    private int generateId() {

        return nextId++;
    }

    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        List<SubTask> subtasksList = epics.get(epic.getId()).getSubTasks();
        epic.setSubTasks(subtasksList);
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(SubTask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic parentEpic = epics.get(subtask.getEpicId());
        if (parentEpic != null) {
            parentEpic.addSubtask(subtask);
            updateEpic(parentEpic);
        }
    }

    public Map<Integer, Task> getTasks() {

        return new HashMap<>(tasks);
    }
    public Map<Integer, Epic> getEpics() {

        return new HashMap<>(epics);
    }

    public Epic getEpic(int id) {

        return epics.get(id);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Boolean updateTaskStatus(int taskId, String newStatus) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return null;
        }
        try {
            TaskStatus status = TaskStatus.valueOf(newStatus.toUpperCase());
            task.setStatus(status);
            System.out.println("Статус задачи обновлён.");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Некорректный статус задачи. " +
                    "\nДопустимые значения: NEW, IN_PROGRESS, DONE.");
            return false;
        }
    }

    public Boolean updateSubTaskStatus(int subTaskId, TaskStatus newStatus) {
        SubTask subTask = subtasks.get(subTaskId);
        if (subTask == null) {
            return false;
        }

        subTask.setStatus(newStatus);
        System.out.println("Статус подзадачи обновлён.");

        Epic parentEpic = epics.get(subTask.getEpicId());
        if (parentEpic != null) {
            TaskStatus newEpicStatus = parentEpic.calculateStatus();
            parentEpic.setStatus(newEpicStatus);
            System.out.println("Статус эпика обновлён на основе статусов подзадач.");
        } else {
            System.out.println("Эпик для подзадачи не найден.");
        }

        return true;
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Задача с ID " + id + " удалена.");
        } else {
            System.out.println("Задача с ID " + id + " не найдена.");
        }
    }

    public void deleteAllTasks() {

        tasks.clear();
    }

    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            for (SubTask subtask : epic.getSubTasks()) {
                tasks.remove(subtask.getId());
            }
        }
        epics.clear();
        System.out.println("Все эпики и связанные подзадачи удалены.");
    }

    public void removeAllSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubTasks().clear();
            System.out.println("Все подзадачи эпика с ID " + epicId + " удалены.");
        } else {
            System.out.println("Эпик с ID " + epicId + " не найден.");
        }
    }

    public boolean removeSubtask(int epicId, int subtaskId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<SubTask> subtasks = epic.getSubTasks();
            return subtasks.removeIf(subtask -> subtask.getId() == subtaskId);
        }
        return false;
    }
}
