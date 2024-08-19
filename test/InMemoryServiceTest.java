import org.junit.jupiter.api.BeforeEach;
import service.InMemoryTaskService;

public class InMemoryServiceTest extends TaskServiceTest {

    @BeforeEach
    public void setUp() {
        taskService = new InMemoryTaskService();
    }
}
