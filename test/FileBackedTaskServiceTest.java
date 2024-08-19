import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import service.FileBackedTaskService;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskServiceTest extends TaskServiceTest {

    private File file;

    @BeforeEach
    public void setUp() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        taskService = new FileBackedTaskService(file);
    }

    @AfterEach
    public void tearDown() {
        file.delete();
    }
}
