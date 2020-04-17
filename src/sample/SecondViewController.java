package sample;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.awt.*;

public class SecondViewController {

    private Controller controller = new Controller();

    @FXML
    private Text mNameUser;

    public void initialize(){
        mNameUser.setText(controller.getmUsernameData());
    }
}
