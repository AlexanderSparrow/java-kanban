package model;

public class Node {
    public Task task;
    public Node prev;
    public Node next;

    public Node(Task task) {
        this.task = task;
    }
}
