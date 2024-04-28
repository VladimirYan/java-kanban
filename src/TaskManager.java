import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

class TaskManager {
    private int taskIdCounter;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, List<Task>> epicSubTasks;
    private final Scanner scanner;

    public TaskManager() {
        this.taskIdCounter = 1;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.epicSubTasks = new HashMap<>();
        this.scanner = new Scanner(System.in);
    }

    // Метод для создания задачи
    public Task createTask(String name, TaskStatus status) {
        Task task = new Task(taskIdCounter++, name, status);
        tasks.put(task.getId(), task);
        return task;
    }

    // Метод для вывода всех задач
    public void printAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст.");
        } else {
            System.out.println("Список задач:");
            for (Task task : tasks.values()) {
                System.out.println(task);
            }
        }
    }

    // Метод для создания эпика
    public Epic createEpic(String name, TaskStatus status) {
        Epic epic = new Epic(taskIdCounter++, name, status);
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
    public SubTask createSubTask(String name, TaskStatus status, int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            SubTask subTask = new SubTask(taskIdCounter++, name, status, epicId);
            epic.addSubTask(subTask);
            if (!epicSubTasks.containsKey(epicId)) {
                epicSubTasks.put(epicId, new ArrayList<>());
            }
            epicSubTasks.get(epicId).add(subTask);
            return subTask;
        } else {
            System.out.println("Эпик с ID " + epicId + " не найден.");
            return null;
        }
    }

    // Метод для получения всех подзадач определенного эпика
    public List<Task> getAllSubTasksForEpic(int epicId) {

        return epicSubTasks.getOrDefault(epicId, new ArrayList<>());
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
        for (Epic epic : epics.values()) {
            for (Task subTask : epic.getSubTasks()) {
                if (subTask.getId() == subTaskId) {
                    subTask.setStatus(newStatus);
                    System.out.println("Статус подзадачи с ID " + subTaskId + " успешно обновлен.");
                    return;
                }
            }
        }
        System.out.println("Подзадача с ID " + subTaskId + " не найдена.");
    }

    // Метод для удаления подзадачи
    public void removeSubTask(int subTaskId) {
        for (Epic epic : epics.values()) {
            for (Task subTask : epic.getSubTasks()) {
                if (subTask.getId() == subTaskId) {
                    epic.getSubTasks().remove(subTask);
                    epicSubTasks.get(epic.getId()).remove(subTask);
                    System.out.println("Подзадача с ID " + subTaskId + " удалена.");
                    return;
                }
            }
        }
        System.out.println("Подзадача с ID " + subTaskId + " не найдена.");
    }

    //Выход из приложения
    public void exit() {
        scanner.close();
        System.out.println("Программа завершена.");
    }
}
