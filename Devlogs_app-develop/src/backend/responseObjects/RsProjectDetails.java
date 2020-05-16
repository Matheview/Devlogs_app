package backend.responseObjects;

import java.util.List;

public class RsProjectDetails extends RsProject {
    private List<User> users;
    private List<Status> statuses;

    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Status> getStatuses() {
        return statuses;
    }
    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }
}
