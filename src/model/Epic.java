package model;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private final Set<SubTask> subTasks;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, name, description);
        this.subTasks = new HashSet<>();
        this.startTime = null;
        this.endTime = null;
        this.duration = Duration.ZERO;
        //updateEpicStatus();
        calculateFields();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subTasks = new HashSet<>();
        this.startTime = null;
        this.endTime = null;
        this.duration = Duration.ZERO;
        //updateEpicStatus();
        calculateFields();
    }


    public void calculateFields() {
        if (subTasks == null || subTasks.isEmpty()) {
            duration = Duration.ZERO;
            startTime = null;
            endTime = null;
            return;
        }

        this.duration = subTasks.stream()
                .map(SubTask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        this.startTime = subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.endTime = subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public Set<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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

}