package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskService extends InMemoryTaskService {
    private final File file;
    private static int currentMaxId = 0;

    public FileBackedTaskService(File file) {
        this.file = file;
        loadFromFile(file);
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(task.toString() + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(epic.toString() + "\n");
                for (SubTask subTask : epic.getSubTasks()) {
                    writer.write(subTask.toString() + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи задачи в файл.", e);
        }
    }

    private FileBackedTaskService loadFromFile(File file) {
      //return new FileBackedTaskService(file);
        try {
            if (!file.exists()) {
                return null;
            }

            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) {
                String[] fields = line.split(",");
                int id = Integer.parseInt(fields[0]);
                TaskType type = TaskType.valueOf(fields[1]);
                currentMaxId = Math.max(currentMaxId, id);  // Обновляем максимальный ID

                switch (type) {
                    case TASK:
                        addTask(Task.fromString(line));
                        break;
                    case EPIC:
                        addEpic(Epic.fromString(line));
                        break;
                    case SUBTASK:
                        int epicId = Integer.parseInt(fields[5]);
                        Epic epic = getEpicById(epicId);
                        if (epic == null) {
                            System.err.println("Epic with ID " + epicId + " not found for SubTask.");
                            continue; // Пропустить эту строку, если эпик не найден
                        }
                        addSubTask(SubTask.fromString(line, epic));
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to load tasks from file.", e);
        }
        return null;
    }

    private int getNextCounter() {
        return ++currentMaxId;
    }

    /*public static FileBackedTaskService loadFromFile(File file) {
        return new FileBackedTaskService(file);
    }*/

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

