package model;

public class SubTask extends Task{
    private final Epic epic;

    public SubTask(int id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d", id, TaskType.SUBTASK, name, status, description, epic.getId());
    }

    public static SubTask fromString(String value, Epic epic) {
        String[] fields = value.split(",");
        return new SubTask(Integer.parseInt(fields[0]), fields[2], fields[4], Status.valueOf(fields[3]), epic);
    }
}

