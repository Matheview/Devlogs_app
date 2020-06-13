package backend.requestObjects;

public class RqNewComment {
    private int user_id;
    private int task_id;
    private String comment_desc;

    public RqNewComment() {}

    public RqNewComment(int user_id, int task_id, String comment_desc) {
        this.user_id = user_id;
        this.task_id = task_id;
        this.comment_desc = comment_desc;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getComment_desc() {
        return comment_desc;
    }

    public void setComment_desc(String comment_desc) {
        this.comment_desc = comment_desc;
    }
}
