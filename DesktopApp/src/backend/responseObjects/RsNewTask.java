package backend.responseObjects;

public class RsNewTask extends BaseResponseObject {
    private int task_id;
    private String task_fullname;
    private String task_title;

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getTask_fullname() {
        return task_fullname;
    }

    public void setTask_fullname(String task_fullname) {
        this.task_fullname = task_fullname;
    }

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }
}
