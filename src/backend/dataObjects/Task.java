package backend.dataObjects;

public class Task {
    private int task_id;
    private String task_name;
    private Integer granted_to;
    private String created_at;
    private String deadline;
    private String priority;
    private int comments_count;

    public int getTask_id() {
        return task_id;
    }
    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getName() {
        return task_name;
    }
    public void setName(String task_name) {
        this.task_name = task_name;
    }

    public Integer getGranted_to() {
        return granted_to;
    }
    public void setGranted_to(Integer granted_to) {
        this.granted_to = granted_to;
    }

    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getComments_count() {
        return comments_count;
    }
    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    @Override
    public String toString() {
        return getName();
    }
}
