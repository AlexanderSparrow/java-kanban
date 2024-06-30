import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryServiceTest {
    private InMemoryHistoryService historyService;
    private Task task1;
    private Task task2;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    public void setUp() {
        historyService = new InMemoryHistoryService();
        task1 = new Task(1, "Task 1", "Description 1");
        task2 = new Task(2, "Task 2", "Description 2");
        epic = new Epic(3, "Epic 1", "Epic Description");
        subTask = new SubTask(4, "SubTask 1", "SubTask Description", Status.NEW, epic);
    }

    @Test
    public void testAddTaskToHistory() {
        historyService.addTaskToHistory(task1);
        List<Task> history = historyService.getTaskHistoryList();
        assertEquals(1, history.size(), "Количество задач в истории не соответствует ожидаемому.");
        assertEquals(task1, history.getFirst(), "Странно, но задачи не идентичны.");
    }

    @Test
    public void testAddDuplicateTaskToHistory() {
        historyService.addTaskToHistory(task1);
        historyService.addTaskToHistory(task1);
        List<Task> history = historyService.getTaskHistoryList();
        assertEquals(1, history.size(), "Количество задач в истории не соответствует ожидаемому.");
        assertEquals(task1, history.getFirst(), "Странно, но задачи не идентичны.");
    }

    @Test
    public void testRemoveTaskFromHistory() {
        historyService.addTaskToHistory(task1);
        historyService.addTaskToHistory(task2);
        historyService.remove(task1.getId());
        List<Task> history = historyService.getTaskHistoryList();
        assertEquals(1, history.size(), "Количество задач в истории не соответствует ожидаемому.");
        assertEquals(task2, history.getFirst(), "Странно, но задачи не идентичны.");
    }

    @Test
    public void testRemoveNonExistentTaskFromHistory() {
        historyService.addTaskToHistory(task1);
        historyService.remove(task2.getId());
        List<Task> history = historyService.getTaskHistoryList();
        assertEquals(1, history.size(), "Количество задач в истории не соответствует ожидаемому.");
        assertEquals(task1, history.getFirst(), "Странно, но задачи не идентичны.");
    }

    @Test
    public void testRemoveAllTasksByType() {
        historyService.addTaskToHistory(task1);
        historyService.addTaskToHistory(epic);
        historyService.addTaskToHistory(subTask);
        historyService.removeAllTaskByType(Task.class);
        List<Task> history = historyService.getTaskHistoryList();
        assertEquals(2, history.size(), "Количество задач в истории не соответствует ожидаемому.");
        assertTrue(history.contains(epic), "Странно, но не все эпики удалились из истории...");
        assertTrue(history.contains(subTask), "Странно, но не все сабтаски удалились из истории...");
    }

    @Test
    public void testGetTaskHistoryList() {
        historyService.addTaskToHistory(task1);
        historyService.addTaskToHistory(task2);
        List<Task> history = historyService.getTaskHistoryList();
        assertNotNull(history, "История пуста.");
        assertEquals(2, history.size(), "Количество задач в истории не соответствует ожидаемому.");
        assertEquals(task1, history.get(0), "Странно, но задачи не идентичны.");
        assertEquals(task2, history.get(1), "Странно, но задачи не идентичны.");
    }

    @Test
    public void testClearHistory() {
        historyService.addTaskToHistory(task1);
        historyService.addTaskToHistory(task2);
        historyService.remove(task1.getId());
        historyService.remove(task2.getId());
        List<Task> history = historyService.getTaskHistoryList();
        assertTrue(history.isEmpty(), "История не пуста.");
    }
}
