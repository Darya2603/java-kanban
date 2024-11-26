package tracker.manager.impl;

import tracker.manager.HistoryManager;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManagerImpl implements HistoryManager {

    private final HashMap<Integer, Node> historyMap = new HashMap<>();
    private final Node head = new Node(null);
    private final Node tail = new Node(null);
    private Node last = head;

    public InMemoryHistoryManagerImpl() {
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        Integer taskId = task.getId();

        if (historyMap.containsKey(taskId)) {
            removeNode(historyMap.get(taskId));
        }

        Node newNode = new Node(task);
        linkLast(newNode);
        historyMap.put(taskId, newNode);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node current = head.next;
        while (current != tail) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            Node nodeToRemove = historyMap.get(id);
            removeNode(nodeToRemove);
            historyMap.remove(id);
        }
    }

    private void linkLast(Node newNode) {
        newNode.prev = last;
        last.next = newNode;
        newNode.next = tail;
        tail.prev = newNode;
        last = newNode;
    }

    private void removeNode(Node node) {
        if (node == null) return;

        node.prev.next = node.next;
        node.next.prev = node.prev;
        // Удаление из linked list завершено
    }

    private static class Node {
        Task task;
        Node next;
        Node prev;

        public Node(Task task) {
            this.task = task;
        }
    }
}



