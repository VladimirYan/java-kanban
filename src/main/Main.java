package main;

import manager.InMemoryHistoryManager;
import tasks.Task;
import tasks.Epic;
import tasks.SubTask;

public class Main {
    public static void main(String[] args) {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        // Создаем две задачи
        Task task1 = new Task(1, "Task 1");
        Task task2 = new Task(2, "Task 2");

        // Создаем эпик с тремя подзадачами
        Epic epic1 = new Epic(3, "Epic 1");
        SubTask subtask1 = new SubTask(4, "Subtask 1", 3);
        SubTask subtask2 = new SubTask(5, "Subtask 2", 3);
        SubTask subtask3 = new SubTask(6, "Subtask 3", 3);

        // Добавляем подзадачи в эпик
        epic1.addSubTask(subtask1);
        epic1.addSubTask(subtask2);
        epic1.addSubTask(subtask3);

        // Создаем эпик без подзадач
        Epic epic2 = new Epic(7, "Epic 2");

        // Добавляем задачи в историю в разном порядке
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task2);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.add(subtask3);
        historyManager.add(epic2);

        printHistory(historyManager, "Начальная история:");

        // Удаляем задачу из истории и проверяем
        historyManager.remove(task2.getId());
        printHistory(historyManager, "Истори после удаления task2:");

        // Удаляем эпик с тремя подзадачами и проверяем
        historyManager.remove(epic1.getId());
        printHistory(historyManager, "История после удаления epic1 и его подзадач:");
    }

    private static void printHistory(InMemoryHistoryManager historyManager, String message) {
        System.out.println(message);
        historyManager.getHistory().forEach(task -> System.out.println(task.getId() + " - " + task.getName()));
        System.out.println();
    }
}
