package backend.requestObjects;

public class RqNewTask {
    private String domain;
    private int project_id;
    private int status_id;
    private int creator_id;
    private String task_name;
    private Integer assigned_to;

    public RqNewTask() {}

    public RqNewTask(String domain, int project_id, int status_id, int creator_id, String task_name, Integer assigned_to) {
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

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
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

    @Override
    public String toString() {
        return "RqNewTask{" +
                "domain='" + domain + '\'' +
                ", project_id=" + project_id +
                ", status_id=" + status_id +
                ", creator_id=" + creator_id +
                ", task_name='" + task_name + '\'' +
                ", assigned_to=" + assigned_to +
                '}';
    }
}
