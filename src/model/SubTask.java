package model;

import java.util.Objects;

public class SubTask extends Task{
    Epic epic;

    public SubTask(int id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
        this.epic.getSubTasks().add(this);
        this.epic.setStatus(epic.getStatus());
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}

