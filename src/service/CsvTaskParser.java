package service;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CsvTaskParser {

    // Метод для преобразования Task в CSV-строку
    public static String toStringCSV(Task task) {
        if (task instanceof SubTask subTask) {
            return String.format("%d,%s,%s,%s,%s,%d,%s,%s,%d",
                    subTask.getId(),
                    TaskType.SUBTASK,
                    subTask.getName(),
                    subTask.getDescription(),
                    subTask.getStatus(),
                    subTask.getDuration().toMinutes(),
                    subTask.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    subTask.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    subTask.getEpicId());  // Добавляем ID эпика
        } else if (task instanceof Epic epic) {
            return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                    epic.getId(),
                    TaskType.EPIC,
                    epic.getName(),
                    epic.getDescription(),
                    epic.getStatus(),
                    epic.getDuration().toMinutes(),
                    epic.getStartTime() != null ? epic.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "1970-01-01 00:00",
                    epic.getEndTime() != null ? epic.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "1970-01-01 00:00"
            );
        } else {
            return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                    task.getId(),
                    TaskType.TASK,
                    task.getName(),
                    task.getDescription(),
                    task.getStatus(),
                    task.getDuration().toMinutes(),  // Преобразование Duration в минуты
                    task.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),  // Форматирование LocalDateTime
                    task.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));  // Форматирование LocalDateTime
        }
    }

    // Метод для создания Task из CSV-строки
    public static Task fromCsvString(String value) {
        String[] fields = value.split(",");

        if (fields.length < 6) {
            throw new IllegalArgumentException("Недостаточно полей в CSV строке: " + value);
        }

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        String description = fields[3];
        Status status = Status.valueOf(fields[4]);
        Duration duration = Duration.ofMinutes(Long.parseLong(fields[5]));
        LocalDateTime startTime = LocalDateTime.parse(fields[6], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime endTime = fields.length > 7 ? LocalDateTime.parse(fields[7], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null;

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, duration, startTime);
            case EPIC:
                return new Epic(id, name, description, duration, startTime, endTime);
            case SUBTASK:
                if (fields.length < 8) {
                    throw new IllegalArgumentException("Недостаточно полей в CSV строке для SubTask: " + value);
                }
                int epicId = Integer.parseInt(fields[8]);
                // Мы не можем создать подзадачу без эпика, поэтому epic должен быть передан извне
                // Пропустим этот случай, так как он обрабатывается другим методом
                return null; // или бросаем исключение
            default:
                throw new IllegalArgumentException("Неверный тип задачи: " + type);
        }
    }

    // Метод для создания SubTask из CSV-строки с передачей объекта Epic
    public static SubTask fromCsvString(String value, Epic epic) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        Status status = Status.valueOf(fields[4]);
        String description = fields[3];
        Duration duration = Duration.ofMinutes(Long.parseLong(fields[5]));
        LocalDateTime startTime = LocalDateTime.parse(fields[6], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        return new SubTask(id, name, description, status, duration, startTime, epic);
    }
}
