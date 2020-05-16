package backend.responseObjects;

import java.util.List;

public class Status {
    private int id;
    private String status;
    private List<Task> tasks;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return status;
    }
    public void setName(String status) {
        this.status = status;
    }

    public List<Task> getTasks() {
        return tasks;
    }
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
