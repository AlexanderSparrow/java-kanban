package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskService extends InMemoryTaskService {
    private final File file;

    public FileBackedTaskService(File file) {
        this.file = file;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(CsvTaskParser.toStringCSV(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(CsvTaskParser.toStringCSV(epic) + "\n");
                for (SubTask subTask : epic.getSubTasks()) {
                    writer.write(CsvTaskParser.toStringCSV(subTask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи задачи в файл.", e);
        }
    }

    public static FileBackedTaskService loadFromFile(File file) {
        FileBackedTaskService fileBackedTaskService = new FileBackedTaskService(file);
        int currentMaxId = 0; // Локальная переменная для хранения максимального ID

        try {
            if (!file.exists()) {
                return fileBackedTaskService;
            }

            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) {  // Пропускаем заголовок
                String[] fields = line.split(",");
                int id = Integer.parseInt(fields[0]);
                TaskType type = TaskType.valueOf(fields[1]);
                currentMaxId = Math.max(currentMaxId, id);  // Обновляем максимальный ID

                switch (type) {
                    case TASK:
                        fileBackedTaskService.addTask(CsvTaskParser.fromCsvString(line));
                        break;
                    case EPIC:
                        fileBackedTaskService.addEpic((Epic) CsvTaskParser.fromCsvString(line));
                        break;
                    case SUBTASK:
                        int epicId = Integer.parseInt(fields[5]);
                        Epic epic = fileBackedTaskService.getEpicById(epicId);
                        if (epic == null) {
                            System.err.println("Epic with ID " + epicId + " not found for SubTask.");
                            continue; // Пропустить эту строку, если эпик не найден
                        }
                        fileBackedTaskService.addSubTask(CsvTaskParser.fromCsvString(line, epic));
                        break;
                }
            }
            fileBackedTaskService.setIdCounter(currentMaxId); // Присваиваем максимальный ID счетчику

        } catch (IOException e) {
            throw new ManagerSaveException("Failed to load tasks from file.", e);
        }
        return fileBackedTaskService;
    }

    private void setIdCounter(int maxId) {
        // Этот метод устанавливает значение счетчика ID в менеджере задач
        setCounter(maxId); // Вызываем метод установки счетчика из InMemoryTaskService или родительского класса
    }

    private int getNextCounter() {
        return (super.getNextId() + 1); // Используем метод получения следующего ID из родительского класса
    }

    @Override
    public void addTask(Task task) {
        task.setId(getNextCounter());
        super.addTask(task);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(getNextCounter());
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(getNextCounter());
        super.addEpic(epic);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }
    public static void main(String[] args) {
        File file = new File("tasks.csv");

        // 1. Создадим несколько задач, эпиков и подзадач
        FileBackedTaskService manager = new FileBackedTaskService(file);

        Task task1 = new Task(1, "Name 1", "Description 1",Status.NEW);
        Task task2 = new Task(2, "Name 2", "Description 2",Status.IN_PROGRESS);

        Epic epic1 = new Epic(3,"Epic Name 1","Epic Description 1");
        Epic epic2 = new Epic(4, "Epic Name 2","Epic Description 2");

        SubTask subTask1 = new SubTask(5, "SubTask Name 1","SubTask Description 1", Status.NEW, epic1);
        SubTask subTask2 = new SubTask(6, "SubTask Name 1","SubTask Description 2", Status.DONE, epic1);
        SubTask subTask3 = new SubTask(7, "SubTask Name 1","SubTask Description 3", Status.IN_PROGRESS, epic2);

        manager.addTask(task1);
        manager.addTask(task2);

        manager.addEpic(epic1);
        manager.addEpic(epic2);

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        // 2. Создадим новый FileBackedTaskService и загрузим данные из файла
        FileBackedTaskService loadedManager = FileBackedTaskService.loadFromFile(file);

        // 3. Проверим, что все задачи, эпики и подзадачи восстановились корректно
        List<Task> tasks = loadedManager.getTasks();
        List<Epic> epics = loadedManager.getEpics();
        List<SubTask> subTasks = loadedManager.getSubTasks();

        // Проверка задач
        assert tasks.size() == 2 : "Количество задач не совпадает";
        assert tasks.contains(task1) : "Task 1 не восстановился корректно";
        assert tasks.contains(task2) : "Task 2 не восстановился корректно";

        // Проверка эпиков
        assert epics.size() == 2 : "Количество эпиков не совпадает";
        assert epics.contains(epic1) : "Epic 1 не восстановился корректно";
        assert epics.contains(epic2) : "Epic 2 не восстановился корректно";

        // Проверка подзадач
        assert subTasks.size() == 3 : "Количество подзадач не совпадает";
        assert subTasks.contains(subTask1) : "SubTask 1 не восстановился корректно";
        assert subTasks.contains(subTask2) : "SubTask 2 не восстановился корректно";
        assert subTasks.contains(subTask3) : "SubTask 3 не восстановился корректно";

        // Выводим результат успешного прохождения сценария
        System.out.println("Все задачи, эпики и подзадачи успешно восстановлены.");
    }
}