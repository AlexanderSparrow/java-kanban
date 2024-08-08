package service;

import java.io.File;

public class Services {
    public static InMemoryTaskService getDefault() {
        return new InMemoryTaskService();
    }

    public static HistoryService getDefaultHistory() {
        return new InMemoryHistoryService();
    }

    public static FileBackedTaskService getDefaultFileBackup() {
        return new FileBackedTaskService();
    }

}
