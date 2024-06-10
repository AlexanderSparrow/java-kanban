
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskService;
import service.TaskService;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskServiceTest {
   TaskService taskService;

    @BeforeEach
    public void initService() {
        taskService = new InMemoryTaskService();
}

    @Test
    void testGetTasks() {
            Task newTask = new Task(11, "Test Task#1", "Test Task#1 Description");
        taskService.addTask(newTask);
            assertNotNull(taskService.getTasks(), "Список пуст");
    }

    @Test
    void testGetSubTasks() {
        Epic testEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        taskService.addEpic(testEpic);
        SubTask newSubTask = new SubTask(12, "Test SubTask#1", "Test SubTask#1 Description",
                Status.NEW, testEpic);
        taskService.addSubTask(newSubTask);
        assertNotNull(taskService.getSubTasks(), "Список пуст");
        List<SubTask> list = taskService.getSubTasks();
        assertEquals(1, list.size(), "Размер списка не верен.");
    }

    @Test
    void testGetEpics() {
        Epic newEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        taskService.addEpic(newEpic);
        assertNotNull(taskService.getEpics(), "Список пуст");
    }

    @Test
    void testGetAllSubTasksByEpic() {
        Epic testEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        taskService.addEpic(testEpic);
        SubTask newSubTask = new SubTask(12, "Test SubTask#1", "Test SubTask#1 Description",
                Status.NEW, testEpic);
        taskService.addSubTask(newSubTask);
        assertNotNull(taskService.getAllSubTasksByEpic(testEpic.getId()), "Список пуст");
        Set<SubTask> list = (taskService.getAllSubTasksByEpic(testEpic.getId()));
        assertEquals(list.size(), 1, "Размер списка не верен.");
        list.add(new SubTask(13, "Test SubTask#2", "Test SubTask#2 Description",
                Status.NEW, testEpic));
        assertArrayEquals(list.toArray(), taskService.getAllSubTasksByEpic(testEpic.getId()).toArray(), "Списки не идентичны");

    }

    @Test
    void testAddTask() {
        Task newTask = new Task("Test Task#1", "Test Task#1 Description");
        taskService.addTask(newTask);
        Task savedTask = taskService.getTaskById(1);
        assertNotNull(savedTask, "А таска-то не нашлась...");
        assertEquals(savedTask, newTask, "Это разные таски...");
    }

    @Test
    void testAddSubTask() {
        Epic testEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        taskService.addEpic(testEpic);
        SubTask newSubTask = new SubTask(12, "Test SubTask#1", "Test SubTask#1 Description",
                Status.NEW, testEpic);
        taskService.addSubTask(newSubTask);
        int id = newSubTask.getId();//addSubTask(newSubTask);
        SubTask savedSubTask = taskService.getSubTaskById(id);
        assertNotNull(savedSubTask, "А сабтаска-то не нашлась...");
        assertEquals(savedSubTask, newSubTask, "Это разные сабтаски...");
    }

    @Test
    void testAddEpic() {
        Epic newEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        taskService.addEpic(newEpic);
        Epic savedEpic = taskService.getEpicById(newEpic.getId());
        assertNotNull(savedEpic, "А эпик-то не нашелся...");
        assertEquals(savedEpic, newEpic, "Это разные эпики...");
    }

    @Test
    void testGetTaskById() {
        taskService.addTask(new Task(1,"123", "456"));
        assertEquals(1, taskService.getTaskById(1).getId(), "Неверный id");
    }

    @Test
    void testGetSubTaskById() {
        Epic testEpic = new Epic(11, "Test Epic#1", "Test Epic#1 Description");
        taskService.addEpic(testEpic);
        SubTask newSubTask = new SubTask(12, "Test SubTask#1", "Test SubTask#1 Description",
                Status.NEW, testEpic);
        taskService.addSubTask(newSubTask);
        int id = newSubTask.getId();
        assertEquals(id, taskService.getSubTaskById(newSubTask.getId()).getId(), "Неверный id");
    }

    @Test
    void testGetEpicById() {
        taskService.addEpic(new Epic(1,"123", "456"));
        assertEquals(1, taskService.getEpicById(1).getId(), "Неверный id");
    }

    @Test
    void testRemoveTask() {
        taskService.addTask(new Task(1,"123", "456"));
        assertFalse(taskService.getTasks().isEmpty(), "Список пуст");
        taskService.removeTask(1);
        assertTrue(taskService.getTasks().isEmpty(),  "Список не пустой");
    }

    @Test
    void testRemoveSubTask() {
        taskService.addTask(new Task(1,"123", "456"));
        assertFalse(taskService.getTasks().isEmpty(), "Список пуст");
        taskService.removeTask(1);
        assertTrue(taskService.getTasks().isEmpty(),  "Список не пустой");
    }

    @Test
    void testRemoveEpic() {
        taskService.addEpic(new Epic(1,"123", "456"));
        assertFalse(taskService.getEpics().isEmpty(), "Список пуст");
        taskService.removeEpic(1);
        assertTrue(taskService.getEpics().isEmpty(),  "Список не пустой");
    }

    @Test
    void testUpdateTask() {
        Task newTask = new Task(11, "Test Task#1", "Test Task#1 Description");
        taskService.addTask(newTask);
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
        taskService.addEpic(testEpic);
        SubTask newSubTask = new SubTask(12, "Test SubTask#1", "Test SubTask#1 Description",
                Status.NEW, testEpic);
        taskService.addSubTask(newSubTask);
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
        taskService.addTask(newTask);
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
        taskService.addTask(new Task(1,"123", "456"));
        assertEquals(1, taskService.getTasks().size(), "Список пуст");
        taskService.removeAllTasks();
        assertEquals(0, taskService.getTasks().size(), "Список не пустой");
    }

    @Test
    void testRemoveAllSubTasks() {
        taskService.addTask(new Task(1,"123", "456"));
        assertEquals(1, taskService.getTasks().size(), "Список пуст");
        taskService.removeAllTasks();
        assertEquals(0, taskService.getTasks().size(), "Список не пустой");
    }

    @Test
    void testRemoveAllEpics() {
        taskService.addEpic(new Epic(1,"123", "456"));
        assertEquals(1, taskService.getEpics().size(), "Список пуст");
        taskService.removeAllEpics();
        assertEquals(0, taskService.getEpics().size(), "Список не пустой");
    }
}