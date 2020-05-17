package backend.responseObjects;

public class User {

    private int id;
    private int user_id;
    private String privilege;
    private String shortcut;
    private String user;
    private String name;

    public User(int id, String privilege, String shortcut, String user) {
        this.id = id;
        this.privilege = privilege;
        this.shortcut = shortcut;
        this.user = user;
    }

    public int getId() {
        if (id != 0)
            return id;
        return user_id;
    }

    public void setId(int id) {
        this.id = id;
        this.user_id = id;
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

    public String getName() {
        if (name != null)
            return name;
        return user;
    }

    public void setName(String name) {
        this.name = name;
        this.user = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
