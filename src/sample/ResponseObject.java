package sample;

public class ResponseObject
{
    private String msg;
    private String privilege;
    private boolean success;

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
}
