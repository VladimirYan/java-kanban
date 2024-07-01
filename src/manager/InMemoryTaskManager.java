package manager;

import tasks.Task;
import tasks.Epic;
import tasks.SubTask;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int taskIdCounter = 1;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, SubTask> subTasks;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    public int generateId () {

        return taskIdCounter++;
    }

    @Override
    public List<Task> getHistory() {

        return historyManager.getHistory();
    }

    @Override
    public List<Task> getAllTasks() {

        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {

        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {

        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Task createTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task with this ID already exists.");
        }
        int uniqueId = generateId();
        Task newTask = new Task(uniqueId, task.getName());
        tasks.put(uniqueId, newTask);
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (tasks.containsKey(epic.getId())) {
            throw new IllegalArgumentException("Epic with this ID already exists.");
        }
        int uniqueId = generateId();
        Epic newEpic = new Epic(uniqueId, epic.getName());
        epics.put(uniqueId, newEpic);
        return newEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId()) || tasks.containsKey(subTask.getId())) {
            throw new IllegalArgumentException("SubTask with this ID already exists.");
        }
        int uniqueId = generateId();
        SubTask newSubTask = new SubTask(uniqueId, subTask.getName(), subTask.getEpicId());
        subTasks.put(uniqueId, newSubTask);
        Epic parentEpic = epics.get(subTask.getEpicId());
        if (parentEpic != null) {
            parentEpic.addSubTask(newSubTask);
            updateEpic(parentEpic);
        }
        return newSubTask;
    }

    @Override
    public Task getTask(int id) {
        Task originalTask = tasks.get(id);
        if (originalTask == null) {
            return null;
        }
        historyManager.add(originalTask);
        return new Task(originalTask.getId(),
                originalTask.getName());
    }

    @Override
    public Epic getEpic(int id) {
        Epic originalEpic = epics.get(id);
        if (originalEpic == null) {
            return null;
        }
        historyManager.add(originalEpic);
        return new Epic(originalEpic.getId(),
                originalEpic.getName());
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask originalSubTask = subTasks.get(id);
        if (originalSubTask == null) {
            return null;
        }
        historyManager.add(originalSubTask);
        return new SubTask(originalSubTask.getId(),
                originalSubTask.getName(),
                originalSubTask.getEpicId());
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task with this ID does not exist.");
        }
        historyManager.add(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getId());
        if (currentEpic != null) {
            historyManager.add(currentEpic);
            Epic updatedEpic = new Epic(epic.getId(), epic.getName());
            epics.put(epic.getId(), updatedEpic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask currentSubTask = subTasks.get(subTask.getId());
        if (currentSubTask != null) {
            historyManager.add(currentSubTask);
            SubTask updatedSubTask = new SubTask(subTask.getId(), subTask.getName(), subTask.getEpicId());
            subTasks.put(subTask.getId(), updatedSubTask);
            Epic parentEpic = epics.get(updatedSubTask.getEpicId());
            if (parentEpic != null) {
                parentEpic.addSubTask(updatedSubTask);
                updateEpic(parentEpic);
            }
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (SubTask subTask : epic.getSubTasks()) {
                subTasks.remove(subTask.getId());
                historyManager.remove(subTask.getId());
            }
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.remove(id);
        Epic parentEpic = epics.get(subTask.getEpicId());
        if (parentEpic != null) {
            parentEpic.removeSubTask(subTask);
            updateEpic(parentEpic);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Task subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
        }
        subTasks.clear();
    }

    @Override
    public List<SubTask> getEpicSubtasks(int epicId) {
        return subTasks.values().stream()
                .filter(subTask -> subTask.getEpicId() == epicId)
                .collect(Collectors.toList());
    }
}
