package backend;

public class CurrentlyLoggedAccount {

    private int user_id;
    private String username;
    private String privilege;
    private String domain;

    public CurrentlyLoggedAccount(int user_id, String username, String privilege, String domain) {
        this.user_id = user_id;
        this.username = username;
        this.privilege = privilege;
        this.domain = domain;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "CurrentlyLoggedAccount{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", privilege='" + privilege + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }
}
