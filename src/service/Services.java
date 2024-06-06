package service;

public class Services {
    public static TaskService getDefault (){
        return new TaskService();
    }
}
