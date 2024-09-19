package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Epic epic;  // transient - не будет сериализоваться

    private Integer epicIdTemp;

    public SubTask(int id, String name, String description, Status status, Duration duration,
                   LocalDateTime startTime, Epic epic) {
        super(id, name, description, status, duration, startTime);
        this.epic = epic;
        this.epicIdTemp = epic.getId();
    }

    public SubTask(String name, String description, Status status, Duration duration, LocalDateTime startTime,
                   Epic epic) {
        super(name, description, status, duration, startTime);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public int getEpicId() {
        return epic != null ? epic.getId() : 0; // Возвращаем id epic, если epic не null
    }

    public Integer getEpicIdTemp() {
        return epicIdTemp;
    }

    public void setEpicId(Integer epicId) {
        this.epicIdTemp = epicId;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
