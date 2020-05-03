package backend;

import java.util.List;

public class ResponseObject
{
    private String msg;
    private String privilege;
    private boolean success;
    private int user_id;
    private String username;
    private List<Domain> listOfDomains;
    private List<User> listOfUsers;
    private String token;
    private List<Notifications> notifications;
    private int qty;


    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getPrivilege() {
        return privilege;
    }
    public void setPrivilege(String privilage)
    {
        this.privilege = privilage;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List getListOfDomains() {
        return listOfDomains;
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

    public void setListOfDomains(List<Domain> listOfDomains) {
        this.listOfDomains = listOfDomains;
    }

    public List<User> getListOfUsers() {
        return listOfUsers;
    }

    public void setListOfUsers(List<User> users) {
        this.listOfUsers = users;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "ResponseObject{" +
                "msg='" + msg + '\'' +
                ", privilege='" + privilege + '\'' +
                ", success=" + success +
                ", user_id=" + user_id +
                ", username='" + username + '\'' +
                ", listOfDomains=" + listOfDomains +
                ", listOfUsers=" + listOfUsers +
                ", token='" + token + '\'' +
                '}';
    }
}
