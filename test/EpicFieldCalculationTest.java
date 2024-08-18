import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EpicFieldCalculationTest {
    private Epic epic;

    @BeforeEach
    public void setUp() {
        epic = new Epic(1, "Epic 1", "Description");
    }

    @Test
    public void shouldReturnZeroDurationAndNullTimesWhenNoSubTasks() {
        epic.calculateFields();
        assertEquals(Duration.ZERO, epic.getDuration());
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
    }

    @Test
    public void shouldCalculateCorrectDurationAndTimesForMultipleSubTasks() {
        LocalDateTime now = LocalDateTime.now();
        SubTask subTask1 = new SubTask(2, "SubTask 1", "Description", Status.NEW, Duration.ofHours(1), now, epic);
        SubTask subTask2 = new SubTask(3, "SubTask 2", "Description", Status.NEW, Duration.ofHours(2), now.plusHours(1), epic);

        epic.getSubTasks().add(subTask1);
        epic.getSubTasks().add(subTask2);

        epic.calculateFields();
        assertEquals(Duration.ofHours(3), epic.getDuration());
        assertEquals(now, epic.getStartTime());
        assertEquals(now.plusHours(3), epic.getEndTime());
    }
}
