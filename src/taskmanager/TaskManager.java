package taskmanager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class TaskManager {
    private int taskIdCounter;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    final Scanner scanner;

    public TaskManager() {
        this.taskIdCounter = 1;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.scanner = new Scanner(System.in);
    }

    public Map<Integer, Epic> getEpics() {

        return epics;
    }

    //Метод для создания задачи
    public Task createTask(Task task) {
        task.setId(taskIdCounter++);
        tasks.put(task.getId(), task);
        return task;
    }

    // Метод для получения всех задач
    public List<Task> getAllTasks() {

        return new ArrayList<>(tasks.values());
    }

    // Метод для получения всех эпиков
    public List<Epic> getAllEpics() {

        return new ArrayList<>(epics.values());
    }

    // Метод для получения всех подзадач
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // Метод для создания эпика
    public Epic createEpic(Epic epic) {
        epic.setId(taskIdCounter++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    // Метод для удаления задачи
    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            System.out.println("Задача с ID " + taskId + " удалена.");
        } else {
            System.out.println("Задача с ID " + taskId + " не найдена.");
        }
    }

    // Метод для создания подзадачи внутри эпика
    public SubTask createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            subTask.setId(taskIdCounter++);
            epic.addSubTask(subTask);
            subTasks.put(subTask.getId(), subTask);
            return subTask;
        } else {
            System.out.println("Эпик с ID " + subTask.getEpicId() + " не найден.");
            return null;
        }
    }

    // Метод для получения всех подзадач определенного эпика
    public List<SubTask> getAllSubTasksForEpic(int epicId) {
        List<SubTask> result = new ArrayList<>();
        for (Epic epic : epics.values()) {
            for (Task subTask : epic.getSubTasks()) {
                if (subTask instanceof SubTask castedSubTask) {
                    if (castedSubTask.getEpicId() == epicId) {
                        result.add(castedSubTask);
                    }
                }
            }
        }
        return result;
    }

    //Метод для обновления статуса задачи (обычной)
    public void updateTaskStatus(int taskId, TaskStatus newStatus) {
        if (tasks.containsKey(taskId)) {
            Task task = tasks.get(taskId);
            task.setStatus(newStatus);
            System.out.println("Статус задачи с ID " + taskId + " успешно обновлен.");
        } else {
            System.out.println("Задача с ID " + taskId + " не найдена.");
        }
    }

    // Метод для обновления статуса подзадачи
    public void updateSubTaskStatus(int subTaskId, TaskStatus newStatus) {
        if (subTasks.containsKey(subTaskId)) {
            SubTask subTask = subTasks.get(subTaskId);
            subTask.setStatus(newStatus);
            System.out.println("Статус подзадачи с ID " + subTaskId + " успешно обновлен.");
        } else {
            System.out.println("Подзадача с ID " + subTaskId + " не найдена.");
        }
    }

    // Метод для удаления эпика
    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            epics.remove(epicId);
            subTasks.remove(epicId);
            System.out.println("Эпик с ID " + epicId + " удален.");
        } else {
            System.out.println("Эпик с ID " + epicId + " не найден.");
        }
    }

    //Метод для удаления определённой подзадачи по ID
    public void removeSubTask(int subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            SubTask subTaskToRemove = subTasks.get(subTaskId);
            subTasks.remove(subTaskId);
            for (Epic epic : epics.values()) {
                if (epic.getSubTasks().contains(subTaskToRemove)) {
                    epic.getSubTasks().remove(subTaskToRemove);
                    break;
                }
            }
            System.out.println("Подзадача с ID " + subTaskId + " удалена.");
        } else {
            System.out.println("Подзадача с ID " + subTaskId + " не найдена.");
        }
    }

    // Метод для удаления всех задач
    public void removeAllTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены.");
    }

    // Метод для удаления всех эпиков
    public void removeAllEpics() {
        epics.clear();
        subTasks.clear();
        System.out.println("Все эпики удалены.");
    }

    // Метод для удаления всех подзадач
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
        }
        subTasks.clear();
        System.out.println("Все подзадачи удалены.");
    }
}
