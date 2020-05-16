package backend.responseObjects;

public class Domain {

    private String domain_desc;
    private String domain;
    private int id;

    public Domain(String domain, int id) {
        this.domain = domain;
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getDomain_desc() {
        return domain_desc;
    }
    public void setDomain_desc(String domain_desc) {
        this.domain_desc = domain_desc;
    }

    @Override
    public String toString() {
        if (domain != null)
            return domain;
        else
            return domain_desc;
    }
}
