package manager;

import tasks.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, SubTask> subTasks;
    protected final HistoryManager historyManager;
    protected Set<Task> prioritizedTasks;
    private int taskIdCounter = 1;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
        this.prioritizedTasks = new TreeSet<>(taskComparator);
    }

    public int generateId() {

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
            throw new IllegalArgumentException("Задача с таким ID уже существует.");
        }

        if (isTaskInvalid(task)) {
            throw new IllegalArgumentException("Задача пересекается с существующими задачами.");
        }

        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Integer id = epic.getId();
        if (epics.containsKey(id)) {
            throw new IllegalArgumentException("Эпик с таким ID уже существует.");
        }

        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (tasks.containsKey(subTask.getId()) || subTasks.containsKey(subTask.getId())) {
            throw new IllegalArgumentException("Подзадача с таким ID уже существует.");
        }

        int uniqueId = generateId();

        if (isTaskInvalid(subTask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с имеющимися задачами");
        }

        SubTask newSubTask = new SubTask(uniqueId, subTask.getName(), subTask.getEpicId(), subTask.getDuration(), subTask.getStartTime());

        subTasks.put(uniqueId, newSubTask);
        prioritizedTasks.add(newSubTask);

        Epic parentEpic = epics.get(newSubTask.getEpicId());
        if (parentEpic != null) {
            parentEpic.addSubTask(newSubTask);
            parentEpic.setStatus(parentEpic.calculateStatus());
            parentEpic.createEpicDateTime();
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
        return originalTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask originalSubTask = subTasks.get(id);
        if (originalSubTask == null) {
            return null;
        }
        historyManager.add(originalSubTask);
        return originalSubTask;
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Задача с таким ID отсутствует.");
        }

        Task oldTask = tasks.get(task.getId());
        historyManager.add(oldTask);

        prioritizedTasks.remove(oldTask);

        if (isTaskInvalid(task)) {
            System.out.println("Задача пересекается по времени с имеющимися задачами");
            prioritizedTasks.add(oldTask);
            return;
        }

        Task updatedTask = new Task(task);
        tasks.put(updatedTask.getId(), updatedTask);

        prioritizedTasks.add(updatedTask);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("Эпик с таким ID отсутствует.");
        }
        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            throw new IllegalArgumentException("Подзадача с таким ID отсутствует.");
        }

        SubTask oldSubTask = subTasks.get(subTask.getId());
        historyManager.add(oldSubTask);

        prioritizedTasks.remove(oldSubTask);

        if (isTaskInvalid(subTask)) {
            prioritizedTasks.add(oldSubTask);
            throw new IllegalArgumentException("Подзадача пересекается с существующими задачами.");
        }

        SubTask updatedSubTask = new SubTask(subTask);
        subTasks.put(updatedSubTask.getId(), updatedSubTask);

        prioritizedTasks.add(updatedSubTask);

        Epic parentEpic = epics.get(updatedSubTask.getEpicId());
        if (parentEpic != null) {
            parentEpic.createEpicDateTime();
            parentEpic.addSubTask(updatedSubTask);
            updateEpic(parentEpic);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Задача с таким ID отсутствует.");
        }
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            throw new IllegalArgumentException("Эпик с таким ID отсутствует.");
        }
        Epic saved = epics.get(id);
        for (SubTask subTaskIdForDelete : saved.getSubTasks()) {
            subTasks.remove(subTaskIdForDelete.getId());
            historyManager.remove(subTaskIdForDelete.getId());
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            throw new IllegalArgumentException("Подзадача с таким ID отсутствует.");
        }
        SubTask subTask = subTasks.get(id);
        prioritizedTasks.remove(subTask);
        int savedEpicId = subTask.getEpicId();
        Epic savedEpic = epics.get(savedEpicId);
        subTasks.remove(id);
        savedEpic.deleteSubTask(subTask);
        savedEpic.setStatus(savedEpic.calculateStatus());
        savedEpic.createEpicDateTime();
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
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
            prioritizedTasks.remove(subTask);
            historyManager.remove(subTask.getId());
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Task subTask : subTasks.values()) {
            prioritizedTasks.remove(subTask);
            historyManager.remove(subTask.getId());
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeSubTasksList();
            epic.setStatus(epic.calculateStatus());
            epic.createEpicDateTime();
        }
    }

    @Override
    public List<SubTask> getEpicSubtasks(int epicId) {
        return subTasks.values().stream()
                .filter(subTask -> subTask.getEpicId() == epicId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPrioritizedTasks() {

        return new ArrayList<>(prioritizedTasks);
    }

    Comparator<Task> taskComparator = (o1, o2) -> {
        if (o1.getId() == o2.getId()) {
            return 0;
        }
        if (o1.getStartTime() == null) {
            return 1;
        }
        if (o2.getStartTime() == null) {
            return -1;
        }

        int startTimeComparison = o1.getStartTime().compareTo(o2.getStartTime());
        if (startTimeComparison != 0) {
            return startTimeComparison;
        }

        return Integer.compare(o1.getId(), o2.getId());
    };

    public boolean isTaskInvalid(Task task) {
        if (prioritizedTasks.isEmpty()) {
            return false;
        }

        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        if (start == null) {
            return false;
        }

        for (Task prioritizedTask : prioritizedTasks) {
            LocalDateTime prioritizedStart = prioritizedTask.getStartTime();
            LocalDateTime prioritizedEnd = prioritizedTask.getEndTime();

            if (isOverlapping(start, end, prioritizedStart, prioritizedEnd)) {
                return true;
            }
        }

        return false;
    }

    public boolean isOverlapping(LocalDateTime start, LocalDateTime end, LocalDateTime prioritizedStart, LocalDateTime prioritizedEnd) {
        if (start.isEqual(prioritizedStart) || start.isEqual(prioritizedEnd) ||
                end.isEqual(prioritizedStart) || end.isEqual(prioritizedEnd)) {
            return true;
        }
        if ((start.isAfter(prioritizedStart) && start.isBefore(prioritizedEnd)) ||
                (end.isAfter(prioritizedStart) && end.isBefore(prioritizedEnd))) {
            return true;
        }
        return start.isBefore(prioritizedStart) && end.isAfter(prioritizedEnd);
    }
}
