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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Controller {

    private String mDomainData = "kaczynski123@wp.pl"; // usunąć sztywne dane i pobrać je z bazy.
    private String mUsernameData = "kaczynskiChuj"; // usunąć sztywne dane i pobrać je z bazy.
    private String mPasswordData = "dudakutas123"; // usunąć sztywne dane i pobrać je z bazy.

    private static final String PATH = "file:\\C:\\Users\\Cyprian\\Desktop\\DevslogCSS\\imgs\\";
    @FXML
    private Button mBtnLogin;
    @FXML
    private ImageView mLogoImage;
    @FXML
    private TextField mDomain;
    @FXML
    private ImageView mLoginImage;
    @FXML
    private TextField mUsername;
    @FXML
    private Button mBtnClose;
    @FXML
    private Pane mPopup;
    @FXML
    private Pane mBlurPopup;
    @FXML
    private PasswordField mPassword;


    public void initialize(){
        mLogoImage.setImage(new Image(PATH + "log.png"));
        mLoginImage.setImage(new Image(PATH + "loginImage.png"));

        //Popup visible
        mPopup.setVisible(false);
        mBlurPopup.setVisible(false);



        mBtnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(mDomain.getText().equals(mDomainData) && mUsername.getText().equals(mUsernameData) && mPassword.getText().equals(mPasswordData)) {
                    System.out.println(mDomain.getText());

                    Parent root;

                    try {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(this.getClass().getResource("SecondView.fxml"));
                        Pane pane = loader.load();
                        Scene scene = new Scene(pane);
                        Stage stage = new Stage();
                        stage.setTitle("SecondView");
                        stage.setScene(scene);
                        stage.show();
                        ((Node)(e.getSource())).getScene().getWindow().hide();
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
