package backend;

public class ResponseObject
{
    private String msg;
    private String privilege;
    private boolean success;
    private int user_id;
    private String username;

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
    public int getUserId() {
        return user_id;
    }
    public void setUserId(int userId) {
        this.user_id = userId;
    }
    public String getUsername() { return username; }
    public void setUserName(int userId) {
        this.user_id = userId;
    }
}
