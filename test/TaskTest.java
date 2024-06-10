import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void shouldInstancesOfTheTaskEqualIfTheirIdIsEqual (){
        Task task1 = new Task(1,"123", "456");
        Task task2 = new Task(1,"321", "654");
        Task task3 = new Task(2,"321", "654");
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны.");
        assertEquals(task1.hashCode(), task2.hashCode(), "Задачи с одинаковым id должны быть " +
                "иметь одинаковые hash коды.");
        assertNotEquals(task2, task3, "Задачи с разными id не должны быть равны.");
        assertNotEquals(task1.hashCode(), task3.hashCode(), "Задачи с одинаковым id должны быть " +
                "иметь разные hash коды.");
    }
}
