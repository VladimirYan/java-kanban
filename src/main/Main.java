package main;

import tasks.*;
import manager.*;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Map;
import java.util.List;

public class Main {
    private static TaskManager taskManager = new TaskManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            int command = getUserChoice(scanner);

            switch (command) {
                case 1:
                    createTask();
                    break;
                case 2:
                    createEpic();
                    break;
                case 3:
                    createSubTask();
                    break;
                case 4:
                    printTasks();
                    break;
                case 5:
                    printAllEpicsAndAskForSubtasks();
                    break;
                case 6:
                    updateTaskStatus();
                    break;
                case 7:
                    updateSubTaskStatus();
                    break;
                case 8:
                    deleteTask();
                    break;
                case 9:
                    deleteEpicWithSubTask();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Неизвестная команда. Пожалуйста, попробуйте снова.");
            }
        }
        System.out.println("Работа с программой завершена.");
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\nВыберите действие:");
        System.out.println("1 - Создать задачу");
        System.out.println("2 - Создать Эпик");
        System.out.println("3 - Создать подзадачу для Эпика");
        System.out.println("4 - Показать все задачи");
        System.out.println("5 - Показать все эпики/подзадачи для эпиков");
        System.out.println("6 - Обновить статус задачи");
        System.out.println("7 - Обновить статус подзадачи");
        System.out.println("8 - Удалить задачу");
        System.out.println("9 - Удалить эпик/подзадачу");
        System.out.println("0 - Выход");
    }

    private static int getUserChoice(Scanner scanner) {
        while (true) {
            System.out.print("\nВаш выбор: ");
            String input = scanner.nextLine();
            Integer validChoice = validateInput(input);
            if (validChoice != null) {
                return validChoice;
            }
        }
    }

    private static Integer validateInput(String input) {
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 0) {
                return choice;
            } else {
                System.out.println("Номер команды должен быть положительным числом." +
                        "\nЛибо равен 0, в случае, если Вы хотите завершить программу.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный ввод. Пожалуйста, введите число.");
        }
        return null;
    }

    private static void createTask() {
        System.out.println("Введите название задачи:");
        String name = scanner.nextLine();

        Task task = new Task(0, name, TaskStatus.NEW);
        taskManager. createTask(task);
        System.out.println("Задача создана: " + task);
    }

    private static void createEpic() {
        System.out.println("Введите название Эпика:");
        String name = scanner.nextLine();

        Epic epic = new Epic(0, name, TaskStatus.NEW);
        taskManager.createEpic(epic);
        System.out.println("Эпик создан: " + epic);
    }

    private static void createSubTask() {
        if (taskManager.getEpics().isEmpty()) {
            System.out.println("Нет доступных эпиков! Сперва создайте хотя бы один эпик.");
            return;
        }

        System.out.println("Список доступных эпиков:");
        for (Map.Entry<Integer, Epic> entry : taskManager.getEpics().entrySet()) {
            System.out.println("ID: " + entry.getKey() + ", Название: " + entry.getValue().getName());
        }

        System.out.println("Введите ID Эпика для подзадачи:");
        boolean isCorrect = false;
        int epicId = 0;

        while (!isCorrect) {
            try {
                epicId = scanner.nextInt();
                scanner.nextLine();
                if (taskManager.getEpics().containsKey(epicId)) {
                    isCorrect = true;
                } else {
                    System.out.println("Нет эпика с таким ID. Попробуйте снова:");
                }
            } catch (InputMismatchException e) {
                System.out.println("Неверный ввод! Пожалуйста, введите числовой ID:");
                scanner.nextLine();
            }
        }

        System.out.println("Введите название подзадачи:");
        String name = scanner.nextLine();

        SubTask subTask = new SubTask(0, name, TaskStatus.NEW, epicId);
        taskManager.createSubtask(subTask);
        System.out.println("Подзадача создана: " + subTask);
    }

    private static void printTasks() {
        Map<Integer, Task> tasks = taskManager.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст.");
        } else {
            System.out.println("Список задач: ");
            tasks.values().forEach(System.out::println);
        }
    }

    private static void printAllEpicsAndAskForSubtasks() {
        Map<Integer, Epic> epicsMap = taskManager.getEpics();
        if (epicsMap.isEmpty()) {
            System.out.println("Нет сохраненных эпиков.");
            return;
        }

        System.out.println("Список эпиков:");
        for (Epic epic : epicsMap.values()) {
            System.out.println(epic.toString());
        }

        System.out.println("Хотите посмотреть подзадачи для эпика? (Введите 'да' для продолжения)");
        String response = scanner.nextLine();

        if ("да".equalsIgnoreCase(response.trim())) {
            System.out.println("Введите ID эпика:");
            int epicId;
            try {
                epicId = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ID.");
                return;
            }

            Epic epic = epicsMap.get(epicId);
            if (epic == null) {
                System.out.println("Эпик с таким ID не найден.");
                return;
            }
            List<SubTask> subtasks = epic.getSubTasks();
            if (subtasks.isEmpty()) {
                System.out.println("У данного эпика нет подзадач.");
            } else {
                System.out.println("Подзадачи данного эпика:");
                subtasks.forEach(subtask -> System.out.println(subtask.toString()));
            }
        }
    }

    private static void updateTaskStatus() {
        if (taskManager.getTasks().isEmpty()) {
            System.out.println("Список задач пуст. Нет задач для обновления статуса.");
            return;
        }

        try {
            System.out.println("Введите ID задачи:");
            int id = scanner.nextInt();
            scanner.nextLine();

            Task task = taskManager.getTask(id);
            if (task == null) {
                System.out.println("Задачи с таким ID не существует.");
                return;
            }

            while (true) {
                System.out.println("Введите новый статус задачи (NEW, IN_PROGRESS, DONE):");
                String status = scanner.nextLine();

                try {
                    taskManager.updateTaskStatus(id, status);
                    return;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: ID задачи должен быть числом.");
            scanner.nextLine();
        }
    }

    private static void updateSubTaskStatus() {
        if (taskManager.getEpics().isEmpty()) {
            System.out.println("Нет доступных эпиков для обновления статуса подзадач.");
            return;
        }

        System.out.println("Список доступных эпиков:");
        for (Map.Entry<Integer, Epic> entry : taskManager.getEpics().entrySet()) {
            System.out.println("ID: " + entry.getKey() + ", Название: " + entry.getValue().getName());
        }

        System.out.println("Введите ID эпика, у которого Вы хотите обновить подзадачу:");
        int epicId;
        try {
            epicId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должно быть числом.");
            return;
        }

        Epic epic = taskManager.getEpic(epicId);
        if (epic == null) {
            System.out.println("Эпик с таким ID не найден.");
            return;
        }

        List<SubTask> subtasks = epic.getSubTasks();
        if (subtasks.isEmpty()) {
            System.out.println("У данного эпика нет подзадач.");
            return;
        }

        System.out.println("Список подзадач:");
        for (SubTask subtask : subtasks) {
            System.out.println("ID подзадачи: " + subtask.getId() + ", Название: " + subtask.getName());
        }

        int id = 0;
        boolean validId = false;

        while (!validId) {
            try {
                System.out.println("Введите ID подзадачи для обновления статуса:");
                id = scanner.nextInt();
                scanner.nextLine();
                validId = true;
            } catch (InputMismatchException e) {
                System.out.println("Некорректный формат ID. Пожалуйста, введите целое число.");
                scanner.nextLine();
            }
        }

        while (true) {
            System.out.println("Введите новый статус подзадачи (NEW, IN_PROGRESS, DONE):");
            String statusInput = scanner.nextLine().toUpperCase();
            try {
                TaskStatus newStatus = TaskStatus.valueOf(statusInput);
                Boolean statusUpdated = taskManager.updateSubTaskStatus(id, newStatus);

                if (statusUpdated) {
                    return;
                } else {
                    System.out.println("Подзадача с таким ID не найдена или другие ошибки.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Некорректный статус. " +
                        "\nПожалуйста, введите один из следующих статусов: NEW, IN_PROGRESS, DONE.");
            } catch (NullPointerException e) {
                System.out.println("Подзадача с таким ID не найдена или другие ошибки.");
            }
        }
    }

    private static void deleteTask() {
        System.out.println("Выберите действие:" +
                "\n1 - Удалить задачу по ID" +
                "\n2 - Удалить все задачи");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice == 1) {
                deleteTaskById();
            } else if (choice == 2) {
                clearAllTasks();
            } else {
                System.out.println("Неверный ввод");
            }
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: Нужно вводить только числа.");
            scanner.nextLine();
        }
    }

    private static void deleteTaskById() {
        System.out.println("Введите ID задачи для удаления:");
        try {
            int taskId = scanner.nextInt();
            taskManager.deleteTaskById(taskId);
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: Нужно вводить числовой ID.");
            scanner.nextLine();
        }
    }

    private static void clearAllTasks() {
        System.out.println("Вы действительно хотите удалить все задачи? (да/нет)");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if ("да".equals(confirm)) {
            taskManager.deleteAllTasks();
            System.out.println("Все задачи были удалены.");
        } else {
            System.out.println("Удаление всех задач отменено.");
        }
    }

    private static void deleteEpicWithSubTask() {
        System.out.println("Выберите действие: " +
                "\n1 - Удалить все эпики(в том числе и подзадачи)" +
                "\n2 - Очистить список подзадач для эпика" +
                "\n3 - Удалить подзадачу по ID");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice == 1) {
                removeAllEpics();
            } else if (choice == 2) {
                removeAllSubtasksOfSpecificEpic();
            } else if (choice == 3){
                showAndRemoveSubtaskFromEpic();
            } else {
                System.out.println("Неверный ввод");
            }
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: Нужно вводить только числа.");
            scanner.nextLine();
        }
    }

    private static void removeAllEpics() {
        System.out.println("Вы действительно хотите удалить все эпики(в том числе и подзадачи)" +
                "\nВведите да/нет");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if ("да".equals(confirm)) {
            taskManager.removeAllEpics();
        } else {
            System.out.println("Удаление всех эпиков отменено");
        }
    }

    private static void removeAllSubtasksOfSpecificEpic() {
        if (taskManager.getEpics().isEmpty()) {
            System.out.println("Нет доступных эпиков.");
            return;
        }

        System.out.println("Список всех эпиков:");
        for (Epic epic : taskManager.getEpics().values()) {
            System.out.println("ID: " + epic.getId() + ", Название: " + epic.getName());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите ID эпика, у которого нужно удалить подзадачи:");

        try {
            int epicId = Integer.parseInt(scanner.nextLine());
            taskManager.removeAllSubtasksOfEpic(epicId);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должно быть числом.");
        }
    }

    private static void showAndRemoveSubtaskFromEpic() {
        if (taskManager.getEpics().isEmpty()) {
            System.out.println("Нет доступных эпиков.");
            return;
        }

        System.out.println("Список всех эпиков:");
        for (Epic epic : taskManager.getEpics().values()) {
            System.out.println("ID: " + epic.getId() + ", Название: " + epic.getName());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите ID эпика, у которого хотите удалить подзадачу:");
        int epicId;
        try {
            epicId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должно быть числом.");
            return;
        }

        Epic epic = taskManager.getEpic(epicId);
        if (epic == null) {
            System.out.println("Эпик с таким ID не найден.");
            return;
        }

        List<SubTask> subtasks = epic.getSubTasks();
        if (subtasks.isEmpty()) {
            System.out.println("У данного эпика нет подзадач.");
            return;
        }

        System.out.println("Список подзадач:");
        for (SubTask subtask : subtasks) {
            System.out.println("ID подзадачи: " + subtask.getId() + ", Название: " + subtask.getName());
        }

        System.out.println("Введите ID подзадачи, которую хотите удалить:");
        try {
            int subtaskId = Integer.parseInt(scanner.nextLine());
            boolean removed = taskManager.removeSubtask(epicId, subtaskId);
            if (removed) {
                System.out.println("Подзадача удалена.");
            } else {
                System.out.println("Подзадача с таким ID не найдена.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должно быть числом.");
        }
    }
}
