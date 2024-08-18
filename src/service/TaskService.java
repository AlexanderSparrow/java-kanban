package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public interface TaskService {

    List<Task> getHistory();

    //Получение всех задач
    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    //Список всех задач эпика
    Set<SubTask> getAllSubTasksByEpic(int id);

    // Создание задач, подзадач, эпиков
    void addTask(Task task);

    void addSubTask(SubTask subTask);

    void addEpic(Epic epic);

    //Получение задач, подзадач, эпиков по ID
    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    //Удалить задачу по ID
    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

    //Обновление задач
    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    // Удаление всех задач, подзадач, эпиков
    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();
}
