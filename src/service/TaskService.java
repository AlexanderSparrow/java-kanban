package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.*;

public class TaskService {


    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private int counter = 0;

    private int getNextCounter() {
        return ++counter;
    }

    //Получение всех задач
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    //Список всех задач эпика
    public Set<SubTask> getAllSubTasksByEpic(int id) {
        if (!epics.containsKey(id)) {
            System.out.printf("Эпик с номером %d отсутствует.", id);
            return new HashSet<>();
        }
        return epics.get(id).getSubTasks();
    }


    // Создание задач, подзадач, эпиков
    public void addTask(Task task) {
        int id = getNextCounter();
        task.setId(id);
        tasks.put(id, task);
    }

    public void addSubTask(SubTask subTask) {
        if (!epics.containsValue(subTask.getEpic())) {
            System.out.println("Эпик отсутствует в хранилище. Ничего добавлять не будем, нам нельзя :-).");
            return;
        }
        int id = getNextCounter();
        subTask.setId(id);
        subTasks.put(id, subTask);
        subTask.getEpic().getSubTasks().add(subTask);
        subTask.getEpic().updateEpicStatus();
    }

    public void addEpic(Epic epic) {
        int id = getNextCounter();
        epic.setId(id);
        epics.put(epic.getId(), epic);
    }

    //Получение задач, подзадач, эпиков по ID
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            System.out.println("such subtask not found");
            return null;
        }
        return subTasks.get(id);
    }

    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("such epic not found");
            return null;

        }
        return epics.get(id);
    }

    //Удалить задачу по ID
    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            subTask.getEpic().getSubTasks().remove(subTask);
            subTask.getEpic().updateEpicStatus();
            subTasks.remove(id);
        }
    }

    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            Set<SubTask> epicSubTasks = epic.getSubTasks();
            for (SubTask subTask : epicSubTasks) {
                subTasks.remove(subTask.getId());
            }
        }
        epics.remove(id);
    }

    //Обновление задач
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Такой задачи не существует");
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            SubTask oldSbTask = subTasks.get(subTask.getId());
            subTask.getEpic().getSubTasks().remove(oldSbTask);
            subTask.getEpic().getSubTasks().add(subTask);
            subTask.getEpic().updateEpicStatus();
            subTasks.put(subTask.getId(), subTask);
        } else {
            System.out.println("Такой подзадачи не существует");
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            final Epic savedEpic = epics.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    // Удаление всех задач, подзадач, эпиков
    public void removeAllTasks() {
        tasks.clear();
    }

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
    }

    public void removeAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Список подзадач пуст.");
            return;
        }
        subTasks.clear();
        epics.clear();
    }
}