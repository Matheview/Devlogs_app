package controllers;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import backend.LoginData;
import backend.RequestService;
import backend.ResponseObject;

import java.io.IOException;

public class Controller {

    private static final String PATH_IMAGES = "/sample/imgs/";
    private static final String ADMIN_VIEW = "../views/adminView.fxml";
    private static final String BOSS_VIEW = "../views/bossView.fxml";
    private static final String PM_VIEW = "../views/SecondView.fxml";

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

    //Login function
    public void logowaniePane(String fxml, String title, ResponseObject user){
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource(fxml));
            Pane pane = loader.load();

            Scene scene = new Scene(pane);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);

            BaseController controller =  loader.<BaseController>getController();
            controller.setUser(user);

            stage.show();
        } catch (IOException er) {
            er.printStackTrace();
        }
    }

    //Popup visible
    public void visiblePopUp(Boolean visible){
        mPopup.setVisible(visible);
        mBlurPopup.setVisible(visible);
    }

    // Metoda do wypełniania pól danymi do logowania się jako kierownik (do szybkiego logowania,
    // nie używać jej w finalnej wersji aplikacji, do usunięcia lub zakomentowania)
    private void loginKierownik() {
        mDomain.setText("devslog.pl");
        mUsername.setText("kierownik@devslog.pl");
        mPassword.setText("kierownikhaslo");
    }

    //Views initialize
    public void initialize() {
        mLogoImage.setImage(new Image(PATH_IMAGES + "log.png"));
        mLoginImage.setImage(new Image(PATH_IMAGES + "loginImage.png"));

        // TODO: metoda do usunięcia
        loginKierownik();

        visiblePopUp(false);

        mBtnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                LoginData mDataLogin = new LoginData(mUsername.getText(), mPassword.getText(), mDomain.getText());
                Gson json = new Gson();
                String jsonInputString = json.toJson(mDataLogin);
                RequestService rs = new RequestService();
                ResponseObject ro = rs.logIn(jsonInputString);
                if (ro.getPrivilege() != null && ro.getPrivilege().equals("Administrator") && mDataLogin.getDomain().equals(mDomain.getText()) && mDataLogin.getEmail().equals(mUsername.getText()) && mDataLogin.getPassword().equals(mPassword.getText())) {
                    logowaniePane(ADMIN_VIEW, "Administrator", ro);
                    ((Node)(e.getSource())).getScene().getWindow().hide();
                } else if (ro.getPrivilege() != null && ro.getPrivilege().equals("Kierownik") && mDataLogin.getDomain().equals(mDomain.getText()) && mDataLogin.getEmail().equals(mUsername.getText()) && mDataLogin.getPassword().equals(mPassword.getText())) {
                   logowaniePane(BOSS_VIEW, "Kierownik", ro);
                    ((Node)(e.getSource())).getScene().getWindow().hide();
                } else if (ro.getPrivilege() != null && ro.getPrivilege().equals("Pracownik") && mDataLogin.getDomain().equals(mDomain.getText()) && mDataLogin.getEmail().equals(mUsername.getText()) && mDataLogin.getPassword().equals(mPassword.getText())) {
                    logowaniePane(PM_VIEW, "Pracownik", ro);
                    ((Node)(e.getSource())).getScene().getWindow().hide();
                } else {
                    visiblePopUp(true);
                    mBtnClose.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            visiblePopUp(false);
                        }
                    });
                }
            }
        });
    }
}