package service;

import exceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;
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

    // Метод для сохранения всех данных в файл
    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            putHeader(writer);

            // Сначала сохраняем все задачи
            getTasks().stream()
                    .map(CsvTaskParser::toStringCSV)
                    .forEach(csv -> {
                        try {
                            writer.write(csv + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка при записи задачи в файл.", e);
                        }
                    });

            // Затем сохраняем все эпики
            getEpics().stream()
                    .map(CsvTaskParser::toStringCSV)
                    .forEach(csv -> {
                        try {
                            writer.write(csv + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка при записи эпика в файл.", e);
                        }
                    });

            // Наконец, сохраняем все подзадачи
            getSubTasks().stream()
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

    private static void putHeader(FileWriter writer) throws IOException {
        writer.write("id,type,name,description,status,duration,startTime,endTime,epic\n");
    }

    // Переопределяем методы для добавления задач
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

    // Переопределяем методы для обновления задач
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

    // Метод для загрузки данных из файла
    public static FileBackedTaskService loadFromFile(File file) {
        FileBackedTaskService fileBackedTaskService = new FileBackedTaskService(file);
        int currentMaxId = 0;

        try {
            if (!file.exists()) {
                return fileBackedTaskService;
            }

            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                if (line.isBlank() || line.startsWith("id")) {
                    continue;
                }

                String[] fields = line.split(",");
                int id = Integer.parseInt(fields[0]);
                TaskType type = TaskType.valueOf(fields[1]);
                currentMaxId = Math.max(currentMaxId, id);

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
                    }
                    case SUBTASK -> {
                        int epicId = Integer.parseInt(fields[8]);
                        Epic epic = fileBackedTaskService.epics.get(epicId);
                        if (epic == null) {
                            System.err.println("Epic with ID " + epicId + " not found for SubTask.");
                        } else {
                            SubTask subTask = CsvTaskParser.fromCsvString(line, epic);
                            fileBackedTaskService.subTasks.put(subTask.getId(), subTask);
                            epic.getSubTasks().add(subTask);
                            fileBackedTaskService.prioritizedTasks.add(subTask);
                        }
                    }
                }
            }

            fileBackedTaskService.counter = currentMaxId;

        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка при загрузке задач из файла.", e);
        }
        return fileBackedTaskService;
    }
}
