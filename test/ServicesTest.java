import org.junit.jupiter.api.Test;
import service.HistoryService;
import service.InMemoryHistoryService;
import service.Services;
import service.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServicesTest {

    @Test
    public void testGetDefaultTaskService() {
        // Проверяем, что метод getDefault возвращает объект, который не равен null
        TaskService taskService = Services.getDefault();
        assertNotNull(taskService, "getDefault должен возвращать инициированный экземпляр TaskService");
        assertEquals(TaskService.class, taskService.getClass(), "getDefault должен возвращать экземпляр класса " +
                "TaskService");
    }

    @Test
    public void testGetDefaultHistoryService() {
        // Проверяем, что метод getDefaultHistory возвращает объект, который не равен null
        HistoryService historyService = Services.getDefaultHistory();
        assertNotNull(historyService, "getDefaultHistory должен возвращать инициированный экземпляр HistoryService");
        assertEquals(InMemoryHistoryService.class, historyService.getClass(), "getDefaultHistory должен " +
                "возвращать объект класса InMemoryHistoryService");
    }
}