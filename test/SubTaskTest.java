import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Duration duration = Duration.ofMinutes(5);
    LocalDateTime startTime = LocalDateTime.parse("2024-08-18 10:00", formatter);
    LocalDateTime startTime2 = LocalDateTime.parse("2024-08-18 10:10", formatter);
    LocalDateTime startTime3 = LocalDateTime.parse("2024-08-18 10:20", formatter);



    @Test
    void shouldInstancesOfTheSubTaskEqualIfTheirIdIsEqual (){
        Epic testEpic = new Epic(100,"test", "test");
        SubTask subTask1 = new SubTask(1, "testSubTask1", "testSubTask2Description", Status.NEW,
                duration, startTime, testEpic);
        SubTask subTask2 = new SubTask(1, "testSubTask2", "testSubTask2Description", Status.NEW,
                duration, startTime2, testEpic);
        SubTask subTask3 = new SubTask(2, "testSubTask1", "testSubTask2Description", Status.NEW,
                duration, startTime3, testEpic);
        assertEquals(subTask1, subTask2, "Задачи с одинаковым id должны быть равны.");
        assertEquals(subTask1.hashCode(), subTask2.hashCode(), "Задачи с одинаковым id должны быть " +
                "иметь одинаковые hash коды.");
        assertNotEquals(subTask1, subTask3, "Задачи с разными id не должны быть равны.");
        assertNotEquals(subTask1.hashCode(), subTask3.hashCode(), "Задачи с одинаковым id должны быть " +
                "иметь разные hash коды.");
    }
}

