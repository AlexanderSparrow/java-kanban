package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.*;

public class TaskService {


    private final Map<Integer, Task> tasks;
    private final Map<Integer, SubTask> subTasks;
    private final Map<Integer, Epic> epics;
    private int counter = 0;

    public TaskService() {
        this.tasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.epics = new HashMap<>();
    }

    public int getNextCounter() {
        return ++counter;
    }

    //Получение всех задач
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    //Список всех задач пика
    public Set<SubTask> allSubTasksByEpic(int id) {
         return epics.get(id).getSubTasks();
        }

    // Создание задач, подзадач, эпиков
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        subTask.getEpic().getSubTasks().add(subTask);
        subTask.getEpic().updateEpicStatus();
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    //Получение задач, подзадач, эпиков по ID
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("such task not found");
        }
        return tasks.get(id);
    }

    public SubTask geSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            System.out.println("such subtask not found");
        }
        return subTasks.get(id);
    }

    public Epic getEpicById(int id) {
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
            if (epic.getSubTasks() != null) {
                Set<SubTask> epicSubTasks = epic.getSubTasks();
                for (SubTask subTask : epicSubTasks) {
                    subTasks.remove(subTask.getId());
                }
                epic.getSubTasks().clear();
            }
        }
        epics.remove(id);
    }

    //Обновление задач
    public void updateTask(Task task) {
        addTask(task);
    }

    public void updateSubTask(SubTask subTask) {
        SubTask oldSbTask = subTasks.get(subTask.getId());
        subTask.getEpic().getSubTasks().remove(oldSbTask);
        subTask.getEpic().getSubTasks().add(subTask);
        subTask.getEpic().updateEpicStatus();
        subTasks.put(subTask.getId(), subTask);
    }

    public void updateEpic(Epic epic) {
        addEpic(epic);
    }

    // Удаление всех задач, подзадач, эпиков
    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubTasks() { // Добавление подзадачи в эпик
        if (subTasks.isEmpty()) {
            System.out.println("Список подзадач пуст.");
        }
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
        }
        subTasks.clear();
    }

    public void removeAllEpics() {
        if (epics.isEmpty()) {
            throw new IllegalArgumentException("Список пуст.");
        }
        subTasks.clear();
        epics.clear();
    }


    // Необязательные методы тестирования

    public void createTask(String name, String description, Status status) {
        Task task = new Task(getNextCounter(), name, description, status);
        tasks.put(task.getId(), task);
    }

    public void createTask(String name, String description) {
        Task task = new Task(getNextCounter(), name, description, Status.NEW);
        tasks.put(task.getId(), task);
    }

    public void createEpic(String name, String description) {
        Epic epic = new Epic(getNextCounter(), name, description);
        epics.put(epic.getId(), epic);
    }

    public void createSubTask(String name, String description, Status status, Epic epic) {
        SubTask subTask = new SubTask(getNextCounter(), name, description, status, epic);
        subTasks.put(subTask.getId(), subTask);
    }

    public void createSubTask(String name, String description, Status status, String epicName) {
        Epic epic = (getEpicByName(epicName));
        if (epic == null) {
            throw new IllegalArgumentException("Epic not found");
        }
        SubTask subTask = new SubTask(getNextCounter(), name, description, status, getEpicByName(epicName));
        subTasks.put(subTask.getId(), subTask);
    }

    public void createSubTask(String name, String description, int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Epic not found");
        }
        SubTask subTask = new SubTask(getNextCounter(), name, description, Status.NEW, getEpicById(epicId));
        subTasks.put(subTask.getId(), subTask);//
    }

    public void createSubTask(String name, String description, Status status, int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Epic not found");
        }
        SubTask subTask = new SubTask(getNextCounter(), name, description, status, epic);
        subTasks.put(subTask.getId(), subTask);
    }

    public Epic getEpicByName(String name) {
        Epic foundedEpic = null;
        for (Epic epic : epics.values()) {
            if (epic.getName().equals(name)) {
                foundedEpic = epic;
                break;
            }
        }
        return foundedEpic;
    }

    public void printAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст.");
        }
        System.out.println("Список задач (списком):");
        for (Task task : tasks.values()) {
            System.out.println("ID: " + task.getId() + ", Название: " + task.getName() + ", Описание: "
                    + task.getDescription() + ", Статус: " + task.getStatus());
        }
    }

    public void printAllSubTasksByEpic(Epic epic) {
        Set<SubTask> subTasks = epic.getSubTasks();
        for (SubTask subTask : subTasks) {
            System.out.println("ID: " + subTask.getId() + ", Название: " + subTask.getName() + ", Описание: "
                    + subTask.getDescription() + ", Статус: " + subTask.getStatus());
        }
    }

    public void printAllSubTasks() {
        System.out.println("Список всех подзадач:");
        if (!getSubTasks().isEmpty()) {
            for (SubTask subTask : getSubTasks().values()) {
                System.out.println("ID: " + subTask.getId() + ", Название: " + subTask.getName()
                        + ", Описание: " + subTask.getDescription() + ", Статус: " + subTask.getStatus());
            }
        } else {
            System.out.println("Список подзадач пуст.");
        }
    }

    public void printAllEpics() {
        System.out.println("Список всех эпиков:");
        if (!getEpics().isEmpty()) {
            for (Epic epic : getEpics().values()) {
                System.out.println("ID: " + epic.getId() + ", Название: " + epic.getName() + ", Описание: "
                        + epic.getDescription() + ", Статус: " + epic.getStatus() + ", Подзадачи: " + epic.getSubTasks());
            }
        } else {
            System.out.println("Список эпиков пуст.");
        }
    }
}