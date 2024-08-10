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
    private static int currentMaxId = 0;

    public FileBackedTaskService(File file) {
        this.file = file;
        //loadFromFile(file);
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
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to load tasks from file.", e);
        }
        return fileBackedTaskService;
    }

    private int getNextCounter() {
        return ++currentMaxId;
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
}
