package service;

public class Services {
    public static TaskService getDefault (){
        return new TaskService();
    }

    public static HistoryService getDefaultHistory (){
        return new InMemoryHistoryService();
    }
}
