package model;

public class SubTask extends Task {
    private final Epic epic;

    public SubTask(int id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public int getEpicId() {
        return epic.getId();
    }
}