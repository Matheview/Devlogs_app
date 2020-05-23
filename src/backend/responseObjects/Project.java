package backend.responseObjects;

public class Project {
    private String domain_name;
    private String privilege;
    private int project_id;
    private String project_name;
    private int user_count;

    public String getDomain_name() {
        return domain_name;
    }
    public void setDomain_name(String domain_name) {
        this.domain_name = domain_name;
    }

    public String getPrivilege() {
        return privilege;
    }
    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public int getId() {
        return project_id;
    }
    public void setId(int id) {
        this.project_id = id;
    }

    public String getProject_name() {
        return project_name;
    }
    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public int getUser_count() {
        return user_count;
    }
    public void setUser_count(int user_count) {
        this.user_count = user_count;
    }

    @Override
    public String toString() {
        return project_name;
    }
}
