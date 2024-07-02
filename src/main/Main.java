package main;

import manager.*;
import tasks.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        // Создание временного файла для сценария
        Path tempFile = Files.createTempFile("user-scenario", ".csv");

        // Инициализация менеджера с использованием временного файла
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), tempFile.toFile());

        // Создание задач, эпиков и подзадач
        Task task1 = new Task(1, "Task 1");
        Task task2 = new Task(2, "Task 2");
        Epic epic1 = new Epic(3, "Epic 1");
        Epic epic2 = new Epic(4, "Epic 2");
        SubTask subTask1 = new SubTask(5, "SubTask 1", epic1.getId());
        SubTask subTask2 = new SubTask(6, "SubTask 2", epic1.getId());

        // Добавление задач, эпиков и подзадач в менеджер
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        // Сохранение задач в файл
        manager.save();

        // Создание нового менеджера из того же файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile.toFile());

        // Проверка существования задач, эпиков и подзадач в новом менеджере
        assert loadedManager.getAllTasks().size() == 2;
        assert loadedManager.getAllEpics().size() == 2;
        assert loadedManager.getAllSubTasks().size() == 2;

        // Проверка, совпадают ли ID исходных и загруженных задач, эпиков и подзадач
        assert task1.getId() == loadedManager.getTask(task1.getId()).getId();
        assert task2.getId() == loadedManager.getTask(task2.getId()).getId();
        assert epic1.getId() == loadedManager.getEpic(epic1.getId()).getId();
        assert epic2.getId() == loadedManager.getEpic(epic2.getId()).getId();
        assert subTask1.getId() == loadedManager.getSubTask(subTask1.getId()).getId();
        assert subTask2.getId() == loadedManager.getSubTask(subTask2.getId()).getId();

        // Удаление временного файла после выполнения сценария
        Files.deleteIfExists(tempFile);
    }
}
