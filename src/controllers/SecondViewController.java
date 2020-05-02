package controllers;

import backend.ResponseObject;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class SecondViewController extends BaseController {

    private Controller controller = new Controller();

    @FXML
    private Text mNameUser;
    @FXML
    private Text mPrivilegeUser;

    public void initialize(){

    }

    @Override
    public void setUser(ResponseObject user) {
        super.setUser(user);
        mNameUser.setText(user.getUsername());
        mPrivilegeUser.setText(user.getPrivilege());
    }

    public void drawElements() {

    }
}
