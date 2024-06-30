package service;

public class Services {
    public static InMemoryTaskService getDefault() {
        return new InMemoryTaskService();
    }

    public static HistoryService getDefaultHistory() {
        return new InMemoryHistoryService();
    }
}
