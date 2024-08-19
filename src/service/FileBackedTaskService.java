package service;

import exceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs(); // Создаем директорию, если она не существует
                file.createNewFile(); // Создаем файл
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при создании файла", e);
            }
        }

        FileBackedTaskService service = new FileBackedTaskService(file);

        try {
            List<String> lines = Files.readAllLines(Paths.get(file.toURI()));

            // Пропускаем первую строку, если это заголовок
            if (!lines.isEmpty() && lines.getFirst().contains("id")) {
                lines.removeFirst();
            }

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                Task task = CsvTaskParser.fromCsvString(line);

                try {
                    if (task instanceof SubTask subTask) {
                        Epic epic = service.getEpicById(subTask.getEpicId());
                        task = CsvTaskParser.fromCsvString(line, epic);
                        service.addSubTask(subTask);
                    } else if (task instanceof Epic epic) {
                        service.addEpic(epic);
                    } else if (task instanceof Task) {
                        service.addTask(task);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка при добавлении задачи: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки данных", e);
        }

        return service;
    }

}
