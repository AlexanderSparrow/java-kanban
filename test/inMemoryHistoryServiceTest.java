import model.Task;
import org.junit.jupiter.api.Test;
import service.inMemoryHistoryService;

import static org.junit.jupiter.api.Assertions.*;

class inMemoryHistoryServiceTest {

    @Test
    void getTaskHistoryList() {
        inMemoryHistoryService history = new inMemoryHistoryService();
        history.addTaskToHistory(new Task("111","222"));
        model.Task[] tasks = history.getTaskHistoryList();
        assertNotNull(tasks, "tasks is null"); // Текст ошибки предложен идеей. Мы ей верим:-)
        assertTrue(tasks.length > 0, "tasks is empty"); // Текст ошибки предложен идеей. Мы ей верим:-)
    }

    @Test
    void addTaskToHistory() {
        inMemoryHistoryService history = new inMemoryHistoryService();
        history.addTaskToHistory(new Task("111","222"));
        model.Task[] tasks = history.getTaskHistoryList();
        assertNotNull(tasks, "tasks is null"); // Текст ошибки предложен идеей. Мы ей верим:-)
        assertTrue(tasks.length > 0, "tasks is empty"); // Текст ошибки предложен идеей. Мы ей верим:-)
    }
}