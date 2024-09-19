package ServiceTests;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskService;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskServiceTest {

    protected TaskService taskService;

    @BeforeEach
    public abstract void setUp() throws IOException;

    @Test
    public void testAddTask() {
        Task task = new Task("Task1", "Description1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        taskService.addTask(task);

        List<Task> tasks = taskService.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    public void testAddSubTask() {
        Epic epic = new Epic(1, "Epic1", "Epic Description");
        taskService.addEpic(epic);

        SubTask subTask = new SubTask(1, "SubTask1", "SubTask Description",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic);
        taskService.addSubTask(subTask);

        List<SubTask> subTasks = taskService.getSubTasks();
        assertEquals(1, subTasks.size());
        assertEquals(subTask, subTasks.get(0));
        assertTrue(epic.getSubTasks().contains(subTask));
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic(1, "Epic1", "Epic Description");
        taskService.addEpic(epic);

        List<Epic> epics = taskService.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Task1", "Description1", Status.NEW, Duration.ofMinutes(10),
                LocalDateTime.now());
        taskService.addTask(task);

        Task updatedTask = new Task("Updated Task1", "Updated Description1", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        updatedTask.setId(task.getId());
        taskService.updateTask(updatedTask);

        Task retrievedTask = taskService.getTaskById(task.getId());
        assertEquals(updatedTask, retrievedTask);
    }

    @Test
    public void testUpdateSubTask() {
        // Создаем и добавляем эпик
        Epic epic = new Epic(1, "Epic1", "Epic Description");
        taskService.addEpic(epic);

        // Создаем и добавляем подзадачу
        LocalDateTime now = LocalDateTime.now();
        SubTask subTask = new SubTask(1, "SubTask1", "SubTask Description",
                Status.NEW, Duration.ofMinutes(5), now, epic);
        taskService.addSubTask(subTask);

        // Обновляем подзадачу с другим временем, чтобы избежать пересечений
        LocalDateTime newStartTime = now.plusDays(1); // Обновляем время начала на следующий день
        SubTask updatedSubTask = new SubTask(1, "Updated SubTask1", "Updated SubTask Description",
                Status.NEW, Duration.ofMinutes(5), newStartTime, epic);
        updatedSubTask.setId(subTask.getId());
        taskService.updateSubTask(updatedSubTask);

        // Проверяем обновление подзадачи
        SubTask retrievedSubTask = taskService.getSubTaskById(subTask.getId());
        assertEquals(updatedSubTask, retrievedSubTask);
    }

    @Test
    public void testUpdateEpic() {
        Epic epic = new Epic(1, "Epic1", "Epic Description");
        taskService.addEpic(epic);

        Epic updatedEpic = new Epic(1, "Updated Epic1", "Updated Description");
        updatedEpic.setId(epic.getId());
        taskService.updateEpic(updatedEpic);

        Epic retrievedEpic = taskService.getEpicById(epic.getId());
        assertEquals(updatedEpic, retrievedEpic);
    }

    @Test
    public void testRemoveTask() {
        Task task = new Task("Task1", "Description1", Status.NEW, Duration.ofMinutes(10),
                LocalDateTime.now().plusHours(1));
        taskService.addTask(task);
        taskService.removeTask(task.getId());

        Task retrievedTask = taskService.getTaskById(task.getId());
        assertNull(retrievedTask);
    }

    @Test
    public void testRemoveSubTask() {
        Epic epic = new Epic(1, "Epic1", "Epic Description");
        taskService.addEpic(epic);

        SubTask subTask = new SubTask(1, "SubTask1", "SubTask Description",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic);
        taskService.addSubTask(subTask);
        taskService.removeSubTask(subTask.getId());

        SubTask retrievedSubTask = taskService.getSubTaskById(subTask.getId());
        assertNull(retrievedSubTask);
        assertFalse(epic.getSubTasks().contains(subTask));
    }

    @Test
    public void testRemoveEpic() {
        Epic epic = new Epic(1, "Epic1", "Epic Description");
        taskService.addEpic(epic);

        SubTask subTask = new SubTask(1, "SubTask1", "SubTask Description",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic);
        taskService.addSubTask(subTask);

        taskService.removeEpic(epic.getId());

        Epic retrievedEpic = taskService.getEpicById(epic.getId());
        assertNull(retrievedEpic);

        SubTask retrievedSubTask = taskService.getSubTaskById(subTask.getId());
        assertNull(retrievedSubTask);
    }

    @Test
    public void testGetPrioritizedTasks() {
        Task task1 = new Task("Task1", "Description1", Status.NEW, Duration.ofHours(1),
                LocalDateTime.now().plusHours(1));
        Task task2 = new Task("Task2", "Description2", Status.NEW, Duration.ofHours(1),
                LocalDateTime.now().plusDays(1).plusHours(1));

        taskService.addTask(task1);
        taskService.addTask(task2);

        List<Task> prioritizedTasks = taskService.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size());
        assertTrue(prioritizedTasks.get(0).getStartTime().isBefore(prioritizedTasks.get(1).getStartTime()));
    }

    @Test
    public void testTimeOverlap() {
        Task task1 = new Task("Task1", "Description1", Status.NEW, Duration.ofHours(2),
                LocalDateTime.now().plusHours(2));
        Task task2 = new Task("Task2", "Description2", Status.NEW, Duration.ofHours(1),
                LocalDateTime.now().plusHours(4));
        Task task3 = new Task("Task3", "Description3", Status.NEW, Duration.ofHours(1),
                LocalDateTime.now().plusHours(3)); // Пересекается с task2

        taskService.addTask(task1);
        taskService.addTask(task2);

        // Теперь task3 пересекается по времени с task2
        assertThrows(IllegalArgumentException.class, () -> taskService.addTask(task3));
    }

    @Test
    public void testTimeOverlapNoOverlap() {
        Task task1 = new Task("Task1", "Description1", Status.NEW, Duration.ofHours(2),
                LocalDateTime.now().plusHours(2));
        Task task2 = new Task("Task2", "Description2", Status.NEW, Duration.ofHours(1),
                LocalDateTime.now().plusHours(5)); // Не пересекается с task1

        taskService.addTask(task1);

        // task2 не должен пересекаться по времени с task1
        assertDoesNotThrow(() -> taskService.addTask(task2));
    }

    @Test
    public void testTimeOverlapExactStartEnd() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime startTimePlusHourPlusMinute = startTime.plusHours(1).plusMinutes(1);
        Task task1 = new Task("Task1", "Description1", Status.NEW, Duration.ofHours(1), startTime);
        Task task2 = new Task("Task2", "Description2", Status.NEW, Duration.ofHours(1), startTimePlusHourPlusMinute); // Начинается, когда task1 заканчивается

        taskService.addTask(task1);

        // task2 начинается, когда task1 заканчивается, не должно быть пересечения
        assertDoesNotThrow(() -> taskService.addTask(task2));
    }
}
