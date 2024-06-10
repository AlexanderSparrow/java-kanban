import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryServiceTest {

    @Test
    void testGetTaskHistoryList() {
        InMemoryHistoryService history = new InMemoryHistoryService();
        history.addTaskToHistory(new Task("111","222"));
        List<Task> listened = history.getTaskHistoryList();
        assertNotNull(listened, "История пуста.");
        assertEquals(1, listened.size(), "Количество задач в истории не соответствует ожидаемому.");
    }

    @Test
    void testAddTaskToHistory() {
        InMemoryHistoryService history = new InMemoryHistoryService();
        for (int counter = 0; counter < 20; counter++) {
            history.addTaskToHistory(new Task("task" + counter,"task" + counter + "Description"));
            history.addTaskToHistory(new Epic(counter, "epic" + counter,"epic" + counter + "Description"));
            history.addTaskToHistory(new SubTask(counter, "subTask" + counter,"subTask" + counter +
                    "Description", Status.NEW, new Epic(counter, "epic" + counter,"epic" + counter + "Description")));
        }
        List<Task> listened = history.getTaskHistoryList();
        assertNotNull(listened, "История пуста.");
        assertEquals(10, listened.size(), "Количество задач в истории не соответствует ожидаемому.");
    }
}