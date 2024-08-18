package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private final Epic epic;

    public SubTask(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime, Epic epic) {
        super(id, name, description, status, duration, startTime);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public int getEpicId() {
        return epic.getId();
    }
}