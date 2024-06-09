import model.Epic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldInstancesOfTheEpicEqualIfTheirIdIsEqual (){
        Epic testEpic1 = new Epic(1,"testEpic1", "testEpic1Description");
        Epic testEpic2 = new Epic(1,"testEpic2", "testEpic2Description");
        Epic testEpic3 = new Epic(2,"testEpic1", "testEpic1Description");

        assertEquals(testEpic1, testEpic2, "Задачи с одинаковым id должны быть равны.");
        assertEquals(testEpic1.hashCode(), testEpic2.hashCode(), "Задачи с одинаковым id должны быть " +
                "иметь одинаковые hash коды.");
        assertNotEquals(testEpic1, testEpic3, "Задачи с разными id не должны быть равны.");
        assertNotEquals(testEpic1.hashCode(), testEpic3.hashCode(), "Задачи с одинаковым id должны быть " +
                "иметь разные hash коды.");
    }



}