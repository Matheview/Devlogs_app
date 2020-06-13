package backend.responseObjects;

import backend.dataObjects.Domain;
import backend.dataObjects.Notifications;
import backend.dataObjects.User;

import java.util.List;

public class ResponseObject
{
    private String msg;
    private String privilege;
    private boolean success;
    private int user_id;
    private String username;
    private List<Domain> listOfDomains;
    private List<User> users;
    private String token;
    private List<Notifications> notifications;
    private int qty;
    private String link;


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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Notifications> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notifications> notifications) {
        this.notifications = notifications;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
                ", listOfUsers=" + users +
                ", token='" + token + '\'' +
                '}';
    }
}
