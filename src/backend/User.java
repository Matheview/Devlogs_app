package backend;

public class User {

    private int id;
    private String privilege;
    private String shortcut;
    private String user;

    public User(int id, String privilege, String shortcut, String user) {
        this.id = id;
        this.privilege = privilege;
        this.shortcut = shortcut;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return user;
    }
}
