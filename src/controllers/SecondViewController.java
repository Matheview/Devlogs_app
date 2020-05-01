package controllers;

import backend.ResponseObject;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class SecondViewController {

    private Controller controller = new Controller();

    @FXML
    private Text mNameUser;
    @FXML
    private Text mPrivilegeUser;

    private ResponseObject user;

    public void initialize(){
    }
    
    public ResponseObject getUser() {
        return user;
    }
    public void setUser(ResponseObject user) {
        this.user = user;
        mNameUser.setText(user.getUsername());
        mPrivilegeUser.setText(user.getPrivilege());
    }
}
