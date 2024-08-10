package service;

import model.*;

public class CsvTaskParser {

    // Метод для преобразования Task в CSV-строку
    public static String toStringCSV(Task task) {
        if (task instanceof SubTask subTask) {
            return String.format("%d,%s,%s,%s,%s,%d",
                    subTask.getId(),
                    TaskType.SUBTASK,
                    subTask.getName(),
                    subTask.getStatus(),
                    subTask.getDescription(),
                    subTask.getEpicId());
        } else if (task instanceof Epic) {
            return String.format("%d,%s,%s,%s,%s",
                    task.getId(),
                    TaskType.EPIC,
                    task.getName(),
                    task.getStatus(),
                    task.getDescription());
        } else {
            return String.format("%d,%s,%s,%s,%s",
                    task.getId(),
                    TaskType.TASK,
                    task.getName(),
                    task.getStatus(),
                    task.getDescription());
        }
    }

    // Метод для создания Task из CSV-строки
    public static Task fromCsvString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        return switch (type) {
            case TASK -> new Task(id, name, description, status);
            case EPIC -> new Epic(id, name, description);
            default -> throw new IllegalArgumentException("Неверный тип задачи: " + type);
        };
    }

    // Метод для создания SubTask из CSV-строки с передачей объекта Epic
    public static SubTask fromCsvString(String value, Epic epic) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        return new SubTask(id, name, description, status, epic);
    }
}
