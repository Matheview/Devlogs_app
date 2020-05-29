package backend.requestObjects;

/**
 * Klasa do tworzenia obiektów, które są wykożystywane w ciele zapytania http
 */
public class RqStatus {
    private String domain;
    private int project_id;
    private int status_id;
    private String status_desc;
    private int creator_id;

    public RqStatus() {}

    public RqStatus(String domain, int project_id, String status_desc, int creator_id) {
        this.domain = domain;
        this.project_id = project_id;
        this.status_desc = status_desc;
        this.creator_id = creator_id;
    }

    public RqStatus(String domain, int project_id, int status_id, String status_desc, int creator_id) {
        this.domain = domain;
        this.project_id = project_id;
        this.status_id = status_id;
        this.status_desc = status_desc;
        this.creator_id = creator_id;
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

    public String getStatus_desc() {
        return status_desc;
    }

    public void setStatus_desc(String status_desc) {
        this.status_desc = status_desc;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }
}
