package backend.responseObjects;

public class Task {
    private int id;
    private String task_name;
    private String granted_to;
    private String created_at;
    private String deadline;
    private String priority;
    private int comments_count;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return task_name;
    }
    public void setName(String task_name) {
        this.task_name = task_name;
    }

    public String getGranted_to() {
        return granted_to;
    }
    public void setGranted_to(String granted_to) {
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
}
