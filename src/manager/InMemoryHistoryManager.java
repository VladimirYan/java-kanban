package manager;

import tasks.*;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    private static class Node {
        Task task;
        Node next;
        Node prev;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            historyList.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return historyList;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        remove(task.getId());

        Node newNode = new Node(tail, task, null);
        if (tail != null) {
            tail.next = newNode;
        }
        tail = newNode;
        if (head == null) {
            head = newNode;
        }

        historyMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = historyMap.get(id);
        if (nodeToRemove != null) {
            Task taskToRemove = nodeToRemove.task;
            if (taskToRemove instanceof Epic epic) {
                for (SubTask subtask : epic.getSubTasks()) {
                    remove(subtask.getId());
                }
            }
            removeNode(nodeToRemove);
            historyMap.remove(id);
        }
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}
