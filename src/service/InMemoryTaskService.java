package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskService implements TaskService {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected int counter = 0;

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(new TaskStartTimeComparator());

    protected final HistoryService historyService = Services.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyService.getTaskHistoryList();
    }

    private int getNextCounter() {
        return ++counter;
    }

    //Получение всех задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    //Список всех задач эпика
    @Override
    public Set<SubTask> getAllSubTasksByEpic(int id) {
        if (!epics.containsKey(id)) {
            System.out.printf("Эпик с номером %d отсутствует.", id);
            return new HashSet<>();
        }
        return epics.get(id).getSubTasks();
    }

    private boolean isTimeOverlap(Task newTask, Task existingTask) {
        LocalDateTime newTaskStart = newTask.getStartTime();
        LocalDateTime newTaskEnd = newTask.getEndTime();
        LocalDateTime existingTaskStart = existingTask.getStartTime();
        LocalDateTime existingTaskEnd = existingTask.getEndTime();

        if (newTaskStart == null || newTaskEnd == null || existingTaskStart == null || existingTaskEnd == null) {
            return false;
        }

        // Проверяем, пересекаются ли временные интервалы
        return !(newTaskEnd.isBefore(existingTaskStart) || newTaskStart.isAfter(existingTaskEnd));
    }

    private boolean hasTimeOverlap(Task newTask) {
        return prioritizedTasks.stream()
                .filter(existingTask -> !existingTask.equals(newTask)) // Исключаем саму задачу
                .anyMatch(existingTask -> isTimeOverlap(newTask, existingTask));
    }

    @Override
    public void addTask(Task task) {
        if (hasTimeOverlap(task)) {
            // Логирование ошибки и проброс исключения
            System.err.println("Ошибка добавления задачи: Задача пересекается по времени с другой задачей.");
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }

        int id = getNextCounter();
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (!epics.containsValue(subTask.getEpic())) {
            System.out.println("Эпик отсутствует в хранилище. Ничего добавлять не будем, нам нельзя :-).");
            return;
        }
        if (hasTimeOverlap(subTask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой задачей или подзадачей.");
        }
        int id = getNextCounter();
        subTask.setId(id);
        subTasks.put(id, subTask);
        Epic epic = subTask.getEpic();
        epic.getSubTasks().add(subTask);
        epic.updateEpicStatus();
        epic.calculateFields();
        prioritizedTasks.add(subTask);
    }

    @Override
    public int addEpic(Epic epic) {
        int id = getNextCounter();
        epic.setId(id);
        epics.put(epic.getId(), epic);
        // Эпики обычно не имеют времени выполнения, поэтому проверка не требуется
        return id;
    }

    //Получение задач, подзадач, эпиков по ID
    @Override
    public Task getTaskById(int id) {
        historyService.addTaskToHistory(tasks.get(id));
        return tasks.get(id);

    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyService.addTaskToHistory(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyService.addTaskToHistory(epics.get(id));
        return epics.get(id);
    }

    //Удалить задачу по ID
    @Override
    public void removeTask(int id) {
        Task task = tasks.get(id); // Прямой доступ к хранилищу
        if (task != null) {
            prioritizedTasks.remove(task);
            tasks.remove(id);
        }
    }

    @Override
    public void removeSubTask(int id) {
        SubTask subTask = subTasks.get(id); // Прямой доступ к хранилищу
        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpic().getId());
            epic.getSubTasks().remove(subTask);
            epic.updateEpicStatus();
            epic.calculateFields();// Обновляем поля эпика
            subTasks.remove(id);
            prioritizedTasks.remove(subTask);
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id); // Прямой доступ к хранилищу
        if (epic != null) {
            epic.getSubTasks().forEach(subTask -> {
                subTasks.remove(subTask.getId());
                prioritizedTasks.remove(subTask);
            });
            epics.remove(id);
        }
    }

    //Обновление задач
    @Override
    public void updateTask(Task updatedTask) {
        Task existingTask = tasks.get(updatedTask.getId());
        if (existingTask == null) {
            throw new IllegalArgumentException("Задача с указанным ID не найдена.");
        }
        if (hasTimeOverlap(updatedTask)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }
        prioritizedTasks.remove(existingTask);
        tasks.put(updatedTask.getId(), updatedTask);
        prioritizedTasks.add(updatedTask);
    }

    public void updateSubTask(SubTask updatedSubTask) {
        if (hasTimeOverlap(updatedSubTask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой задачей или подзадачей.");
        }
        int subTaskId = updatedSubTask.getId();
        SubTask oldSubTask = subTasks.get(subTaskId);
        if (oldSubTask != null) {
            prioritizedTasks.remove(oldSubTask);
            Epic epic = oldSubTask.getEpic();
            if (epic != null) {
                epic.getSubTasks().remove(oldSubTask);
                epic.getSubTasks().add(updatedSubTask);
                epic.updateEpicStatus(); // Обновляем статус эпика
                epic.calculateFields();// Обновляем поля эпика
            }
            subTasks.put(subTaskId, updatedSubTask);
            prioritizedTasks.add(updatedSubTask);

        } else {
            throw new IllegalArgumentException("Подзадача с указанным ID не найдена.");
        }
    }

    @Override
    public int updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            final Epic savedEpic = epics.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
            return savedEpic.getId();
        } else {
            System.out.println("Такого эпика не существует");
            throw new RuntimeException("Такого эпика не существует");
        }
    }

    // Удаление всех задач, подзадач, эпиков
    @Override
    public void removeAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        if (subTasks.isEmpty()) {
            System.out.println("Список подзадач пуст.");
            return;
        }
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            epic.updateEpicStatus();
        }
        subTasks.clear();
        prioritizedTasks.removeIf(task -> task instanceof SubTask);
    }

    @Override
    public void removeAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Список подзадач пуст.");
            return;
        }
        for (Epic epic : epics.values()) {
            // Удаляем все подзадачи эпика из приоритетного списка
            prioritizedTasks.removeAll(epic.getSubTasks());
        }
        subTasks.clear();
        epics.clear();
    }

    private static class TaskStartTimeComparator implements Comparator<Task> {
        @Override
        public int compare(Task task1, Task task2) {
            if (task1.getStartTime() == null) {
                return 1;
            }
            if (task2.getStartTime() == null) {
                return -1;
            }
            return task1.getStartTime().compareTo(task2.getStartTime());
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

}
