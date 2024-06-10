import org.junit.jupiter.api.Test;
import service.HistoryService;
import service.InMemoryHistoryService;
import service.Services;
import service.InMemoryTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServicesTest {

    @Test
    public void testGetDefaultTaskService() {
        // Проверяем, что метод getDefault возвращает объект, который не равен null
        InMemoryTaskService inMemoryTaskService = Services.getDefault();
        assertNotNull(inMemoryTaskService, "getDefault должен возвращать инициированный экземпляр InMemoryTaskService");
        assertEquals(InMemoryTaskService.class, inMemoryTaskService.getClass(), "getDefault должен возвращать экземпляр класса " +
                "InMemoryTaskService");
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