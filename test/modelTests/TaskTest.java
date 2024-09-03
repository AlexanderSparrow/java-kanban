package modelTests;

import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Duration duration = Duration.ofMinutes(120);
    LocalDateTime startTime = LocalDateTime.parse("2024-08-18 10:00", formatter);


    @Test
    void shouldInstancesOfTheTaskEqualIfTheirIdIsEqual (){
        Task task1 = new Task(1,"123", "456");
        Task task2 = new Task(1,"321", "654");
        Task task3 = new Task(2,"321", "654");
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны.");
        assertEquals(task1.hashCode(), task2.hashCode(), "Задачи с одинаковым id должны " +
                "иметь одинаковые hash коды.");
        assertNotEquals(task2, task3, "Задачи с разными id не должны быть равны.");
        assertNotEquals(task1.hashCode(), task3.hashCode(), "Задачи с разными id должны " +
                "иметь разные hash коды.");
    }
}
