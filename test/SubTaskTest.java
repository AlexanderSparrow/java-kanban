import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    void shouldInstancesOfTheSubTaskEqualIfTheirIdIsEqual (){
        Epic testEpic = new Epic(100,"test", "test");
        SubTask subTask1 = new SubTask(1, "testSubTask1", "testSubTask2Description", Status.NEW, testEpic);
        SubTask subTask2 = new SubTask(1, "testSubTask2", "testSubTask2Description", Status.NEW, testEpic);
        SubTask subTask3 = new SubTask(2, "testSubTask1", "testSubTask2Description", Status.NEW, testEpic);
        assertEquals(subTask1, subTask2, "Задачи с одинаковым id должны быть равны.");
        assertEquals(subTask1.hashCode(), subTask2.hashCode(), "Задачи с одинаковым id должны быть " +
                "иметь одинаковые hash коды.");
        assertNotEquals(subTask1, subTask3, "Задачи с разными id не должны быть равны.");
        assertNotEquals(subTask1.hashCode(), subTask3.hashCode(), "Задачи с одинаковым id должны быть " +
                "иметь разные hash коды.");
    }
}

