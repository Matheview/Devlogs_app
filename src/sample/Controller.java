package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    public String getmUsernameData() {
        return mUsernameData;
    }

    private String mDomainData = "123"; // usunąć sztywne dane i pobrać je z bazy.
    private String mUsernameData = "Kaczyński"; // usunąć sztywne dane i pobrać je z bazy.
    private String mPasswordData = "123"; // usunąć sztywne dane i pobrać je z bazy.

    private static final String PATH_IMAGES = "/sample/imgs/";

    @FXML
    private PasswordField mPassword;
    @FXML
    private TextField mUsername;
    @FXML
    private TextField mDomain;
    @FXML
    private ImageView mLogoImage;
    @FXML
    private ImageView mLoginImage;
    @FXML
    private Button mBtnClose;
    @FXML
    private Button mBtnLogin;
    @FXML
    private Pane mPopup;
    @FXML
    private Pane mBlurPopup;


    public void initialize(){
        mLogoImage.setImage(new Image(PATH_IMAGES + "log.png"));
        mLoginImage.setImage(new Image(PATH_IMAGES + "loginImage.png"));

        //Popup visible
        mPopup.setVisible(false);
        mBlurPopup.setVisible(false);

        mBtnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(mDomain.getText().equals(mDomainData) && mUsername.getText().equals(mUsernameData) && mPassword.getText().equals(mPasswordData)) {
                    Parent root;
                    try {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(this.getClass().getResource("/sample/SecondView.fxml"));
                        Pane pane = loader.load();
                        Scene scene = new Scene(pane);
                        Stage stage = new Stage();
                        stage.setTitle("SecondView");
                        stage.setScene(scene);
                        stage.show();
                    }
                    catch (IOException er){
                        er.printStackTrace();
                    }
                } else {
                   mPopup.setVisible(true);
                    mBlurPopup.setVisible(true);
                   mBtnClose.setOnAction(new EventHandler<ActionEvent>() {
                       @Override
                       public void handle(ActionEvent actionEvent) {
                           mPopup.setVisible(false);
                           mBlurPopup.setVisible(false);
                       }
                   });
                }
            }
        });
    }
}
