import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskServiceTest {
    private InMemoryTaskService taskService;

    @BeforeEach
    public void setUp() {
        taskService = new InMemoryTaskService();
    }

    @Test
    public void shouldAddAndGetTask() {
        Task task = new Task("Task 1", "Description 1");
        taskService.addTask(task);

        Task savedTask = taskService.getTaskById(task.getId());
        assertNotNull(savedTask, "Task should be saved");
        assertEquals(task, savedTask, "Saved task should be equal to original task");
    }

    @Test
    public void shouldAddAndGetSubTask() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        taskService.addEpic(epic);

        SubTask subTask = new SubTask(2, "SubTask 1", "Description", Status.NEW, Duration.ofHours(1), LocalDateTime.now(), epic);
        taskService.addSubTask(subTask);

        SubTask savedSubTask = taskService.getSubTaskById(subTask.getId());
        assertNotNull(savedSubTask, "SubTask should be saved");
        assertEquals(subTask, savedSubTask, "Saved subtask should be equal to original subtask");

        Set<SubTask> subTasks = taskService.getAllSubTasksByEpic(epic.getId());
        assertTrue(subTasks.contains(subTask), "Epic should contain the subtask");
    }

    @Test
    public void shouldAddAndGetEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        taskService.addEpic(epic);

        Epic savedEpic = taskService.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Epic should be saved");
        assertEquals(epic, savedEpic, "Saved epic should be equal to original epic");
    }

    @Test
    public void shouldUpdateTask() {
        Task task = new Task("Task 1", "Description 1");
        taskService.addTask(task);

        task.setName("Updated Task 1");
        task.setDescription("Updated Description 1");
        taskService.updateTask(task);

        Task updatedTask = taskService.getTaskById(task.getId());
        assertNotNull(updatedTask, "Task should be updated");
        assertEquals("Updated Task 1", updatedTask.getName(), "Task name should be updated");
        assertEquals("Updated Description 1", updatedTask.getDescription(), "Task description should be updated");
    }

    @Test
    public void shouldUpdateSubTask() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        taskService.addEpic(epic);

        SubTask subTask = new SubTask(2, "SubTask 1", "Description", Status.NEW, Duration.ofHours(1), LocalDateTime.now(), epic);
        taskService.addSubTask(subTask);

        subTask.setName("Updated SubTask 1");
        subTask.setDescription("Updated Description 1");
        subTask.setStatus(Status.IN_PROGRESS);
        taskService.updateSubTask(subTask);

        SubTask updatedSubTask = taskService.getSubTaskById(subTask.getId());
        assertNotNull(updatedSubTask, "SubTask should be updated");
        assertEquals("Updated SubTask 1", updatedSubTask.getName(), "SubTask name should be updated");
        assertEquals("Updated Description 1", updatedSubTask.getDescription(), "SubTask description should be updated");
        assertEquals(Status.IN_PROGRESS, updatedSubTask.getStatus(), "SubTask status should be updated");
    }

    @Test
    public void shouldUpdateEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        taskService.addEpic(epic);

        epic.setName("Updated Epic 1");
        epic.setDescription("Updated Description 1");
        taskService.updateEpic(epic);

        Epic updatedEpic = taskService.getEpicById(epic.getId());
        assertNotNull(updatedEpic, "Epic should be updated");
        assertEquals("Updated Epic 1", updatedEpic.getName(), "Epic name should be updated");
        assertEquals("Updated Description 1", updatedEpic.getDescription(), "Epic description should be updated");
    }

    @Test
    public void shouldRemoveTask() {
        Task task = new Task("Task 1", "Description 1");
        taskService.addTask(task);

        taskService.removeTask(task.getId());
        Task removedTask = taskService.getTaskById(task.getId());
        assertNull(removedTask, "Task should be removed");
    }

    @Test
    public void shouldRemoveSubTask() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        taskService.addEpic(epic);

        SubTask subTask = new SubTask(2, "SubTask 1", "Description", Status.NEW, Duration.ofHours(1), LocalDateTime.now(), epic);
        taskService.addSubTask(subTask);

        taskService.removeSubTask(subTask.getId());
        SubTask removedSubTask = taskService.getSubTaskById(subTask.getId());
        assertNull(removedSubTask, "SubTask should be removed");

        Set<SubTask> subTasks = taskService.getAllSubTasksByEpic(epic.getId());
        assertFalse(subTasks.contains(subTask), "Epic should no longer contain the subtask");
    }

    @Test
    public void shouldRemoveEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        taskService.addEpic(epic);

        SubTask subTask = new SubTask(2, "SubTask 1", "Description", Status.NEW, Duration.ofHours(1), LocalDateTime.now(), epic);
        taskService.addSubTask(subTask);

        taskService.removeEpic(epic.getId());
        Epic removedEpic = taskService.getEpicById(epic.getId());
        assertNull(removedEpic, "Epic should be removed");

        SubTask removedSubTask = taskService.getSubTaskById(subTask.getId());
        assertNull(removedSubTask, "SubTask associated with the removed Epic should also be removed");
    }

    @Test
    public void shouldRemoveAllTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        taskService.addTask(task1);
        taskService.addTask(task2);

        taskService.removeAllTasks();
        assertTrue(taskService.getTasks().isEmpty(), "All tasks should be removed");
    }

    @Test
    public void shouldRemoveAllSubTasks() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        taskService.addEpic(epic);

        SubTask subTask1 = new SubTask(2, "SubTask 1", "Description", Status.NEW, Duration.ofHours(1), LocalDateTime.now(), epic);
        SubTask subTask2 = new SubTask(3, "SubTask 2", "Description", Status.NEW, Duration.ofHours(1), LocalDateTime.now(), epic);
        taskService.addSubTask(subTask1);
        taskService.addSubTask(subTask2);

        taskService.removeAllSubTasks();
        assertTrue(taskService.getSubTasks().isEmpty(), "All subtasks should be removed");
        assertTrue(epic.getSubTasks().isEmpty(), "All subtasks should be removed from their respective epics");
    }

    @Test
    public void shouldRemoveAllEpics() {
        Epic epic1 = new Epic(1, "Epic 1", "Description");
        Epic epic2 = new Epic(2, "Epic 2", "Description");
        taskService.addEpic(epic1);
        taskService.addEpic(epic2);

        taskService.removeAllEpics();
        assertTrue(taskService.getEpics().isEmpty(), "All epics should be removed");
        assertTrue(taskService.getSubTasks().isEmpty(), "All subtasks associated with epics should also be removed");
    }

    @Test
    public void shouldReturnTaskHistory() {
        Task task = new Task("Task 1", "Description 1");
        taskService.addTask(task);

        taskService.getTaskById(task.getId());
        List<Task> history = taskService.getHistory();
        assertEquals(1, history.size(), "History should contain one task");
        assertEquals(task, history.get(0), "History should contain the added task");
    }
}
