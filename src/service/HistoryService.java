package service;

import model.Task;

public interface HistoryService {

    Task[] getTaskHistoryList();

    void addTaskToHistory(Task addedTask);

}
