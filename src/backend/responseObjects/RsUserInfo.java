package backend.responseObjects;

import java.util.List;

public class RsUserInfo extends BaseResponseObject {

    private String name;
    private String email;
    private String created_at;
    private List<Domain> domains;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public List<Domain> getDomains() {
        return domains;
    }
    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    @Override
    public String toString() {
        return name;
    }
}
