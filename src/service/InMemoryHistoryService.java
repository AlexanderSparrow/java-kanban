package service;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryService implements HistoryService {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public List<Task> getTaskHistoryList() {
        List<Task> taskList = new ArrayList<>();
        Node current = head;
        while (current != null) {
            taskList.add(current.task);
            current = current.next;
        }
        return taskList;
    }

    @Override
    public void addTaskToHistory(Task addedTask) {
        if (addedTask == null) {
            System.out.println("Task is null");
            return;
        }
        if (nodeMap.containsKey(addedTask.getId())) {
            removeNode(nodeMap.get(addedTask.getId()));
        }
        linkLast(addedTask);
        nodeMap.put(addedTask.getId(), tail);
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (tail != null) {
            tail.next = newNode;
            newNode.prev = tail;
        } else {
            head = newNode;
        }
        tail = newNode;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
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
        nodeMap.remove(node.task.getId());
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
        }
    }

    @Override
    public <T> void removeAllTaskByType(Class<? extends T> type) {
        Node currentNode = head;
        while (currentNode != null) {
            Node nextNode = currentNode.next;
            if (currentNode.task.getClass().equals(type)) {
                removeNode(currentNode);
            }
            currentNode = nextNode;
        }
    }
}
