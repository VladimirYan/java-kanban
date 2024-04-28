import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в менеджер задач!");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getUserChoice(scanner);

            switch (choice) {
                case 1:
                    createTask(taskManager, scanner);
                    break;
                case 2:
                    taskManager.printAllTasks();
                    break;
                case 3:
                    removeTask(taskManager, scanner);
                    break;
                case 4:
                    updateTaskStatus(taskManager, scanner);
                    break;
                case 5:
                    createEpic(taskManager, scanner);
                    break;
                case 6:
                    createSubTask(taskManager, scanner);
                    break;
                case 7:
                    getAllSubTasksForEpic(taskManager, scanner);
                    break;
                case 8:
                    updateSubTaskStatus(taskManager, scanner);
                    break;
                case 9:
                    removeSubTask(taskManager, scanner);
                    break;
                case 10:
                    running = false;
                    break;
                default:
                    System.out.println("Некорректный ввод. Пожалуйста, выберите существующий вариант.");
                    break;
            }
        }
        taskManager.exit();
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\nВыберите действие:");
        System.out.println("1. Создать задачу.");
        System.out.println("2. Вывести все задачи.");
        System.out.println("3. Удалить задачу.");
        System.out.println("4. Обновить статус задачи.");
        System.out.println("5. Создать эпик.");
        System.out.println("6. Создать подзадачу.");
        System.out.println("7. Посмотреть все подзадачи для эпика.");
        System.out.println("8. Обновить статус подзадачи.");
        System.out.println("9. Удалить подзадачу.");
        System.out.println("10. Выйти из приложения.");
    }

    private static int getUserChoice(Scanner scanner) {
        System.out.print("\nВаш выбор: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    private static void createTask(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите название задачи:");
        String taskName = scanner.nextLine();
        System.out.println("Введите статус задачи (NEW, IN_PROGRESS, DONE):");
        TaskStatus taskStatus = TaskStatus.valueOf(scanner.nextLine().toUpperCase());

        Task newTask = new Task(0, taskName, taskStatus);
        Task createdTask = taskManager.createTask(newTask);

        System.out.println("Успешно создана новая задача: " + createdTask);
    }

    private static void createEpic(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите название эпика:");
        String epicName = scanner.nextLine();
        Epic newEpic = taskManager.createEpic(epicName, TaskStatus.NEW);
        System.out.println("Создан новый эпик: " + newEpic);
    }

    private static void createSubTask(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите название подзадачи:");
        String subTaskName = scanner.nextLine();
        System.out.println("Введите ID эпика:");
        int epicId = scanner.nextInt();
        scanner.nextLine();
        SubTask newSubTask = taskManager.createSubTask(subTaskName, TaskStatus.NEW, epicId);
        if (newSubTask != null) {
            System.out.println("Создана новая подзадача: " + newSubTask);
        }
    }

    private static void getAllSubTasksForEpic(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите ID эпика:");
        int epicIdForSubTasks = scanner.nextInt();
        System.out.println("Подзадачи для эпика с ID " + epicIdForSubTasks + ":");
        for (Task sub : taskManager.getAllSubTasksForEpic(epicIdForSubTasks)) {
            System.out.println(sub.getName() + " - Статус: " + sub.getStatus());
        }
    }

    private static void updateTaskStatus(TaskManager taskManager, Scanner scanner) {
        System.out.println("\nВведите ID задачи, которой нужно обновить статус:");
        int taskId = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Введите новый статус (NEW, IN_PROGRESS, DONE):");
        TaskStatus newStatus = TaskStatus.valueOf(scanner.nextLine().toUpperCase());
        taskManager.updateTaskStatus(taskId, newStatus);
    }

    private static void removeTask(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите ID задачи, которую нужно удалить:");
        int taskToDelete = scanner.nextInt();
        scanner.nextLine();
        taskManager.removeTask(taskToDelete);
    }

    private static void updateSubTaskStatus(TaskManager taskManager, Scanner scanner) {
        System.out.println("\nВведите ID подзадачи, которой нужно обновить статус:");
        int subTaskId = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Введите новый статус (NEW, IN_PROGRESS, DONE):");
        TaskStatus newStatus = TaskStatus.valueOf(scanner.nextLine().toUpperCase());
        taskManager.updateSubTaskStatus(subTaskId, newStatus);
    }

    private static void removeSubTask(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите ID подзадачи, которую нужно удалить:");
        int subTaskIdToDelete = scanner.nextInt();
        scanner.nextLine();
        taskManager.removeSubTask(subTaskIdToDelete);
    }
}
