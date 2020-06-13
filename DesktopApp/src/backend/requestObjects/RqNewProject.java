package backend.requestObjects;

public class RqNewProject {
    private int user_id;
    private String project_name;
    private int domain_id;

    public RqNewProject() {
    }

    public RqNewProject(int user_id, String project_name, int domain_id) {
        this.setUser_id(user_id);
        this.setProject_name(project_name);
        this.setDomain_id(domain_id);
    }

    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getProject_name() {
        return project_name;
    }
    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public int getDomain_id() {
        return domain_id;
    }
    public void setDomain_id(int domain_id) {
        this.domain_id = domain_id;
    }
}
