package model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private final Set<SubTask> subTasks;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subTasks = new HashSet<>();
    }

    public Epic(int id, String name, String description, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, name, description);
        this.subTasks = new HashSet<>();
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    public Set<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public Duration getDuration() {
        // Пересчитываем duration на основе подзадач
        if (subTasks.isEmpty()) {
            return Duration.ZERO;
        }
        return subTasks.stream()
                .map(SubTask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getStartTime() {
        // Пересчитываем startTime на основе подзадач
        if (subTasks.isEmpty()) {
            return null;
        }
        return subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        // Пересчитываем endTime на основе подзадач
        if (subTasks.isEmpty()) {
            return null;
        }
        return subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(endTime);
    }

    public void updateEpicStatus() {
        if (subTasks.isEmpty()) {
            status = Status.NEW;
            return;
        }

        boolean allDone = subTasks.stream()
                .allMatch(subTask -> subTask.getStatus() == Status.DONE);

        boolean allNew = subTasks.stream()
                .allMatch(subTask -> subTask.getStatus() == Status.NEW);

        if (allDone) {
            status = Status.DONE;
        } else if (allNew) {
            status = Status.NEW;
        } else {
            status = Status.IN_PROGRESS;
        }
    }

    public void calculateFields() {
        if (subTasks == null || subTasks.isEmpty()) {
            this.duration = Duration.ZERO;
            this.startTime = null;
            this.endTime = null;
            return;
        }

        // Суммируем продолжительность всех подзадач
        this.setDuration(subTasks.stream()
                .map(SubTask::getDuration)
                .reduce(Duration.ZERO, Duration::plus));

        // Находим минимальное время начала среди всех подзадач
        this.setStartTime(subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null));

        // Находим максимальное время окончания среди всех подзадач
        this.setEndTime(subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null));
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

