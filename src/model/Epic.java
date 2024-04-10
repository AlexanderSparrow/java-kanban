package model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Epic extends Task {
    private static Status status = Status.NEW;
    Set<SubTask> subTasks;

    public Epic(int id, String name, String description) {
        super(id, name, description, status);
        this.subTasks = new HashSet<>();
    }

    public Set <SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public Status getStatus() {
        if (subTasks.isEmpty()) {
            return Status.NEW;
        }
        Set<Status> statuses = subTasks.stream()
                .map(Task::getStatus)
                .collect(Collectors.toSet());
        if (statuses.size() != 1) {
            return Status.IN_PROGRESS;
        } else {
            return statuses.iterator().next();
        }
    }

    public  void updateEpicStatus() {
        status = getStatus();

    }
}
