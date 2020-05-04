package backend.responseObjects;

import java.util.List;

public class RsDomains extends BaseResponseObject {
    private List<Domain> domains;

    public List<Domain> getDomains() {
        return domains;
    }
    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }
}
