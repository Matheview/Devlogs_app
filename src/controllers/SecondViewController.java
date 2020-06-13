package controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class SecondViewController extends BaseController {

    private Controller controller = new Controller();

    @FXML
    private Text mNameUser;
    @FXML
    private Text mPrivilegeUser;

    public void initialize(){
        mPrivilegeUser.setText(Controller.currAcc.getPrivilege());
        
    }

}
