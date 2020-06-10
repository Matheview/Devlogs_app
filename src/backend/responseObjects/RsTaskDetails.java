package backend.responseObjects;

import backend.dataObjects.Comment;
import backend.dataObjects.User;

import java.util.List;

public class RsTaskDetails extends BaseResponseObject {
    private int task_id;
    private String task_name;
    private String task_desc;
    private String deadline;
    private String created_at;
    private String priority_desc;
    private Integer assigned_to;
    private List<User> users;
    private List<Comment> comments;

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_desc() {
        return task_desc;
    }

    public void setTask_desc(String task_desc) {
        this.task_desc = task_desc;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPriority_desc() {
        return priority_desc;
    }

    public void setPriority_desc(String priority_desc) {
        this.priority_desc = priority_desc;
    }

    public Integer getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(Integer assigned_to) {
        this.assigned_to = assigned_to;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
