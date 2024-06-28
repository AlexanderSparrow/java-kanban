package service;

import model.Task;

import java.util.List;

public interface HistoryService {

    List <Task> getTaskHistoryList();

    void addTaskToHistory(Task addedTask);

    void remove(int id);

}
