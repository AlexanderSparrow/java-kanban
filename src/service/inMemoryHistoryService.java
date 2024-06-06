package service;

import model.Task;

public class inMemoryHistoryService implements HistoryService {
    private int historyLength = 0;
    final int MAX_HISTORY_LENGTH = 10;
    private final Task [] taskHistoryList = new Task[MAX_HISTORY_LENGTH];

    @Override
    public Task[] getTaskHistoryList() {
        return taskHistoryList;
    }

    @Override
    public void addTaskToHistory(Task addedTask) {
        if (historyLength >= MAX_HISTORY_LENGTH) {
            for (int i = MAX_HISTORY_LENGTH - 1; i > 0; i--) {
                taskHistoryList[i] = taskHistoryList[i - 1];
            }
            taskHistoryList[0] = addedTask;
        } else {
            for (int i = historyLength; i > 0; i--) {
                taskHistoryList[i] = taskHistoryList[i - 1];
            }
            taskHistoryList[0] = addedTask;
            historyLength++;
        }
    }
}
