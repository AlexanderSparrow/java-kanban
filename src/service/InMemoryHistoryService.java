package service;

import model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryService implements HistoryService {
    final int MAX_HISTORY_LENGTH = 10;
    private final List<Task> taskHistoryList = new ArrayList<>(MAX_HISTORY_LENGTH);

    @Override
    public List <Task> getTaskHistoryList() {
        return taskHistoryList;
    }

    @Override
    public void addTaskToHistory(Task addedTask) {
        if (addedTask == null) {
            System.out.println("Task is null");
            return;
        }
        if (taskHistoryList.size() >= MAX_HISTORY_LENGTH) {
            taskHistoryList.removeLast();
            }
        taskHistoryList.addFirst(addedTask);
    }
}
