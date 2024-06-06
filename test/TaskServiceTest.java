
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;
import service.TaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest extends TaskService {

    @Test
    void testGetTasks() {
            Task newTask = new Task(11, "Test Task#1", "Test Task#1 Description");
            addTask(newTask);
            assertNotNull(getTasks(), "Список пуст");
    }

    @Test
    void testGetSubTasks() {
        Epic testEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        addEpic(testEpic);
        SubTask newSubTask = new SubTask(12, "Test SubTask#1", "Test SubTask#1 Description",
                Status.NEW, testEpic);
        addSubTask(newSubTask);
        assertNotNull(getSubTasks(), "Список пуст");
        List<SubTask> list = getSubTasks();
        assertEquals(1, list.size(), "Размер списка не верен.");
    }

    @Test
    void testGetEpics() {
        Epic newEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        addEpic(newEpic);
        assertNotNull(getEpics(), "Список пуст");
    }

    @Test
    void testGetAllSubTasksByEpic() {
        Epic testEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        addEpic(testEpic);
        SubTask newSubTask = new SubTask(12, "Test SubTask#1", "Test SubTask#1 Description",
                Status.NEW, testEpic);
        addSubTask(newSubTask);
        assertNotNull(getAllSubTasksByEpic(testEpic.getId()), "Список пуст");
        Set<SubTask> list = (getAllSubTasksByEpic(testEpic.getId()));
        assertEquals(list.size(), 1, "Размер списка не верен.");
        list.add(new SubTask(13, "Test SubTask#2", "Test SubTask#2 Description",
                Status.NEW, testEpic));
        assertArrayEquals(list.toArray(), getAllSubTasksByEpic(testEpic.getId()).toArray(), "Списки не идентичны");

    }

    @Test
    void testAddTask() {
        Task newTask = new Task("Test Task#1", "Test Task#1 Description");
        addTask(newTask);
        Task savedTask = getTaskById(1);
        assertNotNull(savedTask, "А таска-то не нашлась...");
        assertEquals(savedTask, newTask, "Это разные таски...");
    }

    @Test
    void testAddSubTask() {
        Epic testEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        addEpic(testEpic);
        SubTask newSubTask = new SubTask(12, "Test SubTask#1", "Test SubTask#1 Description",
                Status.NEW, testEpic);
        addSubTask(newSubTask);
        int id = newSubTask.getId();//addSubTask(newSubTask);
        SubTask savedSubTask = getSubTaskById(id);
        assertNotNull(savedSubTask, "А сабтаска-то не нашлась...");
        assertEquals(savedSubTask, newSubTask, "Это разные сабтаски...");
    }

    @Test
    void testAddEpic() {
        Epic newEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        addEpic(newEpic);
        Epic savedEpic = getEpicById(newEpic.getId());
        assertNotNull(savedEpic, "А эпик-то не нашелся...");
        assertEquals(savedEpic, newEpic, "Это разные эпики...");
    }

    @Test
    void testGetTaskById() {
        addTask(new Task(1,"123", "456"));
        assertEquals(1, getTaskById(1).getId(), "Неверный id");
    }

    @Test
    void testGetSubTaskById() {
        Epic testEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        addEpic(testEpic);
        SubTask newSubTask = new SubTask(12, "Test SubTask#1", "Test SubTask#1 Description",
                Status.NEW, testEpic);
        addSubTask(newSubTask);
        int id = newSubTask.getId();
        assertEquals(id, getSubTaskById(newSubTask.getId()).getId(), "Неверный id");
    }

    @Test
    void testGetEpicById() {
        addEpic(new Epic(1,"123", "456"));
        assertEquals(1, getEpicById(1).getId(), "Неверный id");
    }

    @Test
    void testRemoveTask() {
        addTask(new Task(1,"123", "456"));
        assertFalse(getTasks().isEmpty(), "Список пуст");
        removeTask(1);
        assertTrue(getTasks().isEmpty(),  "Список не пустой");
    }

    @Test
    void testRemoveSubTask() {
        addTask(new Task(1,"123", "456"));
        assertFalse(getTasks().isEmpty(), "Список пуст");
        removeTask(1);
        assertTrue(getTasks().isEmpty(),  "Список не пустой");
    }

    @Test
    void testRemoveEpic() {
        addEpic(new Epic(1,"123", "456"));
        assertFalse(getEpics().isEmpty(), "Список пуст");
        removeEpic(1);
        assertTrue(getEpics().isEmpty(),  "Список не пустой");
    }

    @Test
    void testUpdateTask() {
        Task newTask = new Task(11, "Test Task#1", "Test Task#1 Description");
        addTask(newTask);
        int id = newTask.getId();
        String name = newTask.getName();
        String description = newTask.getDescription();
        String newDescription = "Test Task#1 newDescription";
        newTask.setDescription(newDescription);
        String newName = "Test Task#1 neName";
        newTask.setName(newName);
        assertNotEquals(name, newName, "Новое имя задачи совпадает с предыдущим. С чего бы это?...");
        assertNotEquals(description, newDescription, "Новое описание задачи совпадает с предыдущим. С чего бы это?...");
        assertEquals(id, newTask.getId(), "id изменился, хотя не должен был...");
    }

    @Test
    void testUpdateSubTask() {
        Epic testEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        addEpic(testEpic);
        SubTask newSubTask = new SubTask(12, "Test SubTask#1", "Test SubTask#1 Description",
                Status.NEW, testEpic);
        addSubTask(newSubTask);
        int id = newSubTask.getId();
        String name = newSubTask.getName();
        String description = newSubTask.getDescription();
        String newDescription = "Test Task#1 newDescription";
        newSubTask.setDescription(newDescription);
        String newName = "Test Task#1 neName";
        newSubTask.setName(newName);
        assertNotEquals(name, newName, "Новое имя задачи совпадает с предыдущим. С чего бы это?...");
        assertNotEquals(description, newDescription, "Новое описание задачи совпадает с предыдущим. С чего бы это?...");
        assertEquals(id, newSubTask.getId(), "id изменился, хотя не должен был...");
    }

    @Test
    void testUpdateEpic() {
        Task newTask = new Task(11, "Test Task#1", "Test Task#1 Description");
        addTask(newTask);
        int id = newTask.getId();
        String name = newTask.getName();
        String description = newTask.getDescription();
        String newDescription = "Test Task#1 newDescription";
        newTask.setDescription(newDescription);
        String newName = "Test Task#1 neName";
        newTask.setName(newName);
        assertNotEquals(name, newName, "Новое имя задачи совпадает с предыдущим. С чего бы это?...");
        assertNotEquals(description, newDescription, "Новое описание задачи совпадает с предыдущим. С чего бы это?...");
        assertEquals(id, newTask.getId(), "id изменился, хотя не должен был...");
    }

    @Test
    void testRemoveAllTasks() {
        addTask(new Task(1,"123", "456"));
        assertEquals(1, getTasks().size(), "Список пуст");
        removeAllTasks();
        assertEquals(0, getTasks().size(), "Список не пустой");
    }

    @Test
    void testRemoveAllSubTasks() {
        addTask(new Task(1,"123", "456"));
        assertEquals(1, getTasks().size(), "Список пуст");
        removeAllTasks();
        assertEquals(0, getTasks().size(), "Список не пустой");
    }

    @Test
    void testRemoveAllEpics() {
        addEpic(new Epic(1,"123", "456"));
        assertEquals(1, getEpics().size(), "Список пуст");
        removeAllEpics();
        assertEquals(0, getEpics().size(), "Список не пустой");
    }
}