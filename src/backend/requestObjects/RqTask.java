package backend.requestObjects;

public class RqTask {
    private String domain;
    private int project_id;
    private int task_id;
    private Integer status_id;
    private int creator_id;
    private String task_name;
    private String task_desc;
    private Integer assigned_to;
    private String priority_desc;

    public RqTask() {}

    public RqTask(String domain, int project_id, Integer task_id, int creator_id) {
        this.domain = domain;
        this.project_id = project_id;
        this.task_id = task_id;
        this.creator_id = creator_id;
    }

    public RqTask(String domain, int project_id, int status_id, int creator_id, String task_name, Integer assigned_to) {
        this.domain = domain;
        this.project_id = project_id;
        this.status_id = status_id;
        this.creator_id = creator_id;
        this.task_name = task_name;
        this.assigned_to = assigned_to;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public Integer getStatus_id() {
        return status_id;
    }

    public void setStatus_id(Integer status_id) {
        this.status_id = status_id;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public Integer getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(Integer assigned_to) {
        this.assigned_to = assigned_to;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getTask_desc() {
        return task_desc;
    }

    public void setTask_desc(String task_desc) {
        this.task_desc = task_desc;
    }

    public String getPriority_desc() {
        return priority_desc;
    }

    public void setPriority_desc(String priority_desc) {
        this.priority_desc = priority_desc;
    }

    @Override
    public String toString() {
        return "RqTask{" +
                "domain='" + domain + '\'' +
                ", project_id=" + project_id +
                ", status_id=" + status_id +
                ", creator_id=" + creator_id +
                ", task_name='" + task_name + '\'' +
                ", assigned_to=" + assigned_to +
                '}';
    }
}
