package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class FileBackedTaskService extends InMemoryTaskService {
    private final File file;
    private final TreeSet<Task> prioritizedTasks;

    public FileBackedTaskService(File file) {
        this.file = file;
        this.prioritizedTasks = new TreeSet<>(new TaskStartTimeComparator());
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
                .anyMatch(existingTask -> isTimeOverlap(newTask, existingTask));
    }

    // Метод для получения списка задач в порядке приоритета
    public TreeSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks); // Возвращаем копию TreeSet
    }

    // Метод для сохранения данных в файл
    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,description,status,duration,startTime,endTime,epic\n");

            getTasks().stream()
                    .map(CsvTaskParser::toStringCSV)
                    .forEach(csv -> {
                        try {
                            writer.write(csv + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка при записи задачи в файл.", e);
                        }
                    });

            getEpics().stream()
                    .map(CsvTaskParser::toStringCSV)
                    .forEach(csv -> {
                        try {
                            writer.write(csv + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка при записи эпика в файл.", e);
                        }
                    });

            getEpics().stream()
                    .flatMap(epic -> epic.getSubTasks().stream())
                    .map(CsvTaskParser::toStringCSV)
                    .forEach(csv -> {
                        try {
                            writer.write(csv + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка при записи подзадачи в файл.", e);
                        }
                    });

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи задачи в файл.", e);
        }
    }

//    private void save() {
//        try (FileWriter writer = new FileWriter(file)) {
//            writer.write("id,type,name,description,status,duration,startTime,endTime,epic\n");
//            for (Task task : getTasks()) {
//                writer.write(CsvTaskParser.toStringCSV(task) + "\n");
//            }
//            for (Epic epic : getEpics()) {
//                writer.write(CsvTaskParser.toStringCSV(epic) + "\n");
//                for (SubTask subTask : epic.getSubTasks()) {
//                    writer.write(CsvTaskParser.toStringCSV(subTask) + "\n");
//                }
//            }
//        } catch (IOException e) {
//            throw new ManagerSaveException("Ошибка при записи задачи в файл.", e);
//        }
//    }

    // Метод для загрузки данных из файла

    public static FileBackedTaskService loadFromFile(File file) {
        FileBackedTaskService fileBackedTaskService = new FileBackedTaskService(file);
        TreeSet<Task> prioritizedTasks = new TreeSet<>(new TaskStartTimeComparator());
        AtomicInteger currentMaxId = new AtomicInteger();

        try {
            if (!file.exists()) {
                return fileBackedTaskService;
            }

            List<String> lines = Files.readAllLines(file.toPath());
            lines.stream().skip(1).forEach(line -> {
                String[] fields = line.split(",");
                int id = Integer.parseInt(fields[0]);
                TaskType type = TaskType.valueOf(fields[1]);
                currentMaxId.set(Math.max(currentMaxId.get(), id));

                switch (type) {
                    case TASK -> {
                        Task task = CsvTaskParser.fromCsvString(line);
                        assert task != null;
                        fileBackedTaskService.tasks.put(task.getId(), task);
                        prioritizedTasks.add(task);
                    }
                    case EPIC -> {
                        Epic epic = (Epic) CsvTaskParser.fromCsvString(line);
                        assert epic != null;
                        fileBackedTaskService.epics.put(epic.getId(), epic);
                        prioritizedTasks.add(epic);
                    }
                    case SUBTASK -> {
                        int epicId = Integer.parseInt(fields[8]);
                        Epic epic1 = fileBackedTaskService.getEpicById(epicId);
                        if (epic1 == null) {
                            System.err.println("Epic with ID " + epicId + " not found for SubTask.");
                        } else {
                            SubTask subTask = CsvTaskParser.fromCsvString(line, epic1);
                            fileBackedTaskService.subTasks.put(subTask.getId(), subTask);
                            epic1.getSubTasks().add(subTask);
                            prioritizedTasks.add(subTask);
                        }
                    }
                }
            });

            fileBackedTaskService.counter = currentMaxId.get();

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке задач из файла.", e);
        }
        return fileBackedTaskService;
    }
    /*public static FileBackedTaskService loadFromFile(File file) {
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
                    case TASK -> {
                        Task task = CsvTaskParser.fromCsvString(line);
                        assert task != null;
                        fileBackedTaskService.tasks.put(task.getId(), task);
                        fileBackedTaskService.prioritizedTasks.add(task);
                    }
                    case EPIC -> {
                        Epic epic = (Epic) CsvTaskParser.fromCsvString(line);
                        assert epic != null;
                        fileBackedTaskService.epics.put(epic.getId(), epic);
                        fileBackedTaskService.prioritizedTasks.add(epic);
                    }
                    case SUBTASK -> {
                        int epicId = Integer.parseInt(fields[8]);
                        Epic epic = fileBackedTaskService.getEpicById(epicId);
                        if (epic == null) {
                            System.err.println("Epic with ID " + epicId + " not found for SubTask.");
                            continue;
                        }
                        SubTask subTask = CsvTaskParser.fromCsvString(line, epic);
                        fileBackedTaskService.subTasks.put(subTask.getId(), subTask);
                        subTask.getEpic().getSubTasks().add(subTask);
                        fileBackedTaskService.prioritizedTasks.add(subTask);
                    }
                }
            }
            fileBackedTaskService.counter = currentMaxId; // Присваиваем максимальный ID счетчику

        } catch (IOException e) {
            throw new ManagerSaveException("Failed to load tasks from file.", e);
        }
        return fileBackedTaskService;
    }
*/
    @Override
    public void addTask(Task task) {
        if (hasTimeOverlap(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }
        super.addTask(task);
        prioritizedTasks.add(task);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (hasTimeOverlap(subTask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой задачей или подзадачей.");
        }
        super.addSubTask(subTask);
        prioritizedTasks.add(subTask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        // Эпики обычно не имеют времени выполнения, поэтому проверка не требуется
        super.addEpic(epic);
        prioritizedTasks.add(epic);
        save();
    }

    @Override
    public void removeTask(int id) {
        Task task = getTaskById(id);
        prioritizedTasks.remove(task);
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        SubTask subTask = getSubTaskById(id);
        prioritizedTasks.remove(subTask);
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = getEpicById(id);
        prioritizedTasks.remove(epic);
        super.removeEpic(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        if (hasTimeOverlap(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }
        prioritizedTasks.remove(getTaskById(task.getId())); // Удаляем старую версию задачи
        super.updateTask(task);
        prioritizedTasks.add(task); // Добавляем обновленную версию задачи
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (hasTimeOverlap(subTask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой задачей или подзадачей.");
        }
        prioritizedTasks.remove(getSubTaskById(subTask.getId())); // Удаляем старую версию подзадачи
        super.updateSubTask(subTask);
        prioritizedTasks.add(subTask); // Добавляем обновленную версию подзадачи
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        prioritizedTasks.remove(getEpicById(epic.getId())); // Удаляем старую версию эпика
        super.updateEpic(epic);
        prioritizedTasks.add(epic); // Добавляем обновленную версию эпика
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        prioritizedTasks.clear();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        prioritizedTasks.removeIf(task -> task instanceof SubTask);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        prioritizedTasks.clear();
        save();
    }

    /*public static void main(String[] args) {
        File file = new File("tasks.csv");

        // 1. Создадим несколько задач, эпиков и подзадач
        FileBackedTaskService manager = new FileBackedTaskService(file);

        Task task1 = new Task(1, "Name 1", "Description 1", Status.NEW);
        Task task2 = new Task(2, "Name 2", "Description 2", Status.IN_PROGRESS);

        Epic epic1 = new Epic(3, "Epic Name 1", "Epic Description 1");
        Epic epic2 = new Epic(4, "Epic Name 2", "Epic Description 2");

        SubTask subTask1 = new SubTask(5, "SubTask Name 1", "SubTask Description 1", Status.NEW, epic1);
        SubTask subTask2 = new SubTask(6, "SubTask Name 2", "SubTask Description 2", Status.DONE, epic1);
        SubTask subTask3 = new SubTask(7, "SubTask Name 3", "SubTask Description 3", Status.IN_PROGRESS, epic2);

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
    }*/

    private static class TaskStartTimeComparator implements Comparator<Task> {
        @Override
        public int compare(Task t1, Task t2) {
            LocalDateTime t1Start = t1.getStartTime() != null ? t1.getStartTime() : LocalDateTime.MIN;
            LocalDateTime t2Start = t2.getStartTime() != null ? t2.getStartTime() : LocalDateTime.MIN;

            int result = t1Start.compareTo(t2Start);
            // Если startTime совпадают, сравниваем по ID, чтобы избежать равенства в TreeSet
            if (result == 0) {
                result = Integer.compare(t1.getId(), t2.getId());
            }
            return result;
        }
    }
}