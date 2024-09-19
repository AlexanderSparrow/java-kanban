package modelTests;

import static org.junit.jupiter.api.Assertions.*;

import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class EpicTest {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Duration duration = Duration.ofMinutes(120);
    LocalDateTime startTime = LocalDateTime.parse("2024-08-18 10:00", formatter);

    @Test
    public void testUpdateEpicStatusAllNew() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        SubTask subTask1 = new SubTask(2, "SubTask 1", "Description", Status.NEW, duration, startTime, epic);
        SubTask subTask2 = new SubTask(3, "SubTask 2", "Description", Status.NEW, duration, startTime, epic);
        epic.getSubTasks().add(subTask1);
        epic.getSubTasks().add(subTask2);

        epic.updateEpicStatus();

        assertEquals(Status.NEW, epic.getStatus(), "Epic status should be NEW when all subtasks are NEW.");
    }

    @Test
    public void testUpdateEpicStatusAllDone() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        SubTask subTask1 = new SubTask(2, "SubTask 1", "Description", Status.DONE, duration, startTime, epic);
        SubTask subTask2 = new SubTask(3, "SubTask 2", "Description", Status.DONE, duration, startTime, epic);
        epic.getSubTasks().add(subTask1);
        epic.getSubTasks().add(subTask2);

        epic.updateEpicStatus();

        assertEquals(Status.DONE, epic.getStatus(), "Epic status should be DONE when all subtasks are DONE.");
    }

    @Test
    public void testUpdateEpicStatusMixedStatuses() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        SubTask subTask1 = new SubTask(2, "SubTask 1", "Description", Status.NEW, duration, startTime, epic);
        SubTask subTask2 = new SubTask(3, "SubTask 2", "Description", Status.DONE, duration, startTime, epic);
        SubTask subTask3 = new SubTask(4, "SubTask 3", "Description", Status.IN_PROGRESS, duration, startTime, epic);
        epic.getSubTasks().add(subTask1);
        epic.getSubTasks().add(subTask2);
        epic.getSubTasks().add(subTask3);

        epic.updateEpicStatus();

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status should be IN_PROGRESS when there are mixed subtasks statuses.");
    }

    @Test
    public void testUpdateEpicStatusAllInProgress() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        SubTask subTask1 = new SubTask(2, "SubTask 1", "Description", Status.IN_PROGRESS, duration, startTime, epic);
        SubTask subTask2 = new SubTask(3, "SubTask 2", "Description", Status.IN_PROGRESS, duration, startTime, epic);
        epic.getSubTasks().add(subTask1);
        epic.getSubTasks().add(subTask2);

        epic.updateEpicStatus();

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status should be IN_PROGRESS when all subtasks are IN_PROGRESS.");
    }
}
