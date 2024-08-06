package model;

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private final Set<SubTask> subTasks;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subTasks = new HashSet<>();
    }

    public Set<SubTask> getSubTasks() {
        return subTasks;
    }

    public void updateEpicStatus() {
        boolean allDone = true;
        boolean allNew = true;
        if (subTasks.isEmpty()) {
            status = Status.NEW;
            return;
        }
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subTask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }
        if (allDone) {
            status = Status.DONE;
        } else if (allNew) {
            status = Status.NEW;
        } else {
            status = Status.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", id, TaskType.EPIC, name, status, description);
    }

    public static Epic fromString(String value) {
        String[] fields = value.split(",");
        return new Epic(Integer.parseInt(fields[0]), fields[2], fields[4]);
    }
}
