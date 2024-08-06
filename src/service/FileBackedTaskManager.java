package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskService {
    private final File file;

    public FileBackedTaskManager(File file) {
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

    private void loadFromFile(File file) {
        try {
            if (!file.exists()) {
                return;
            }

            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) {
                String[] fields = line.split(",");
                TaskType type = TaskType.valueOf(fields[1]);
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
                        addSubTask(SubTask.fromString(line, epic));
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения задачи из файла.", e);
        }
    }

    public static FileBackedTaskManager loadFromTestFile(File file) {
        return new FileBackedTaskManager(file);
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
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
}
