package backend.requestObjects;

public class RqUser {
    private int user_id;
    private int project_id;
    private String domain;
    private int assigned_to;

    public RqUser() {}

    public RqUser(int user_id, int project_id, int assigned_to) {
        this.user_id = user_id;
        this.project_id = project_id;
        this.assigned_to = assigned_to;
    }

    public RqUser(int user_id, int project_id, String domain, int assigned_to) {
        this.user_id = user_id;
        this.project_id = project_id;
        this.domain = domain;
        this.assigned_to = assigned_to;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(int assigned_to) {
        this.assigned_to = assigned_to;
    }
}
