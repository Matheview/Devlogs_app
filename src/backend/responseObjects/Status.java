package backend.responseObjects;

import java.util.List;

public class Status {
    private int status_id;
    private String status;
    private List<Task> tasks;

    public int getStatus_id() {
        return status_id;
    }
    public void setStatus_id(int status_id) {
        this.status_id = status_id;
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

    @Override
    public String toString() {
        return getName();
    }
}
