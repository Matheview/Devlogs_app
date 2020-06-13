package backend.responseObjects;

import backend.dataObjects.User;

import java.util.List;

public class RsUsersInDomain extends BaseResponseObject {
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
