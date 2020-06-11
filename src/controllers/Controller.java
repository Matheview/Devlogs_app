package controllers;

import backend.CurrentlyLoggedAccount;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import backend.requestObjects.RequestData;
import backend.RequestService;
import backend.responseObjects.ResponseObject;
import sample.AppInfo;
import utils.DialogsUtils;

import java.io.IOException;

public class Controller extends Application {

    private static final String PATH_IMAGES = "/imgs/";
    private static final String ADMIN_VIEW = "../views/adminView.fxml";
    private static final String BOSS_VIEW = "../views/bossView.fxml";
    private static final String USER_VIEW = "../views/UserView.fxml";

    public static CurrentlyLoggedAccount currAcc;

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
    @FXML
    private Button mRemindPwd;
    @FXML
    private Pane mPopupPwd;
    @FXML
    private Button mClosePopup;
    @FXML
    private Button mGetToken;
    @FXML
    private TextField mEmail;
    @FXML
    private Hyperlink mLink;
    @FXML
    private Text mText;
    @FXML
    private Text mGiveEmailTxt;


    //Login function
    public void logowaniePane(String fxml, String title, ResponseObject user){
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource(fxml));
            Pane pane = loader.load();

            Scene scene = new Scene(pane);
            Stage stage = new Stage();
            stage.setTitle(AppInfo.TITLE + " - " + title);
            stage.getIcons().add(AppInfo.ICON);
            stage.setScene(scene);

            BaseController baseController = loader.getController();
            baseController.setController(this);

            stage.show();
        } catch (IOException er) {
            er.printStackTrace();
        }
    }

    //Popup visible
    public void visibleErrorPopUp(Boolean visible){
        mPopup.setVisible(visible);
        mBlurPopup.setVisible(visible);
    }

    //Popup change password visible
    public void visibleChangePwdPopUp(Boolean visible){
        mBlurPopup.setVisible(visible);
        mPopupPwd.setVisible(visible);
    }

    // Metoda do wypełniania pól danymi do logowania się jako kierownik (do szybkiego logowania,
    // nie używać jej w finalnej wersji aplikacji, do usunięcia lub zakomentowania)
    private void loginKierownik() {
        mDomain.setText("devslog.pl");
        mUsername.setText("kierownik@devslog.pl");
        mPassword.setText("kierownikhaslo");
    }

    public void clearFields() {
        mDomain.setText("");
        mUsername.setText("");
        mPassword.setText("");
    }

    public void showWindow() {
        Scene scene = mBtnClose.getScene();
        if (scene != null) {
            Stage window = (Stage) scene.getWindow();
            if (window != null) {
                window.show();
            }
        }
    }
    //Views initialize
    public void initialize() {
        mPopupPwd.setVisible(false);

        mRemindPwd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                visibleChangePwdPopUp(true);
                mLink.setVisible(false);
//                mPwdImage.setImage(new Image(PATH_IMAGES + "log.png"));
                mClosePopup.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                    visibleChangePwdPopUp(false);
                    }
                });
                mGetToken.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        Gson gson = new Gson();
                        RequestData data = new RequestData(mEmail.getText());
                        String jsonInputString = gson.toJson(data);
                        RequestService rs = new RequestService();
                        ResponseObject ro = rs.requestChangePassword(jsonInputString);
                        if(ro.isSuccess()){
                            mEmail.setVisible(false);
                            mLink.setVisible(true);
                            mLink.setText(ro.getLink());
                            mGetToken.setVisible(false);
                            mText.setVisible(true);
                            mGiveEmailTxt.setVisible(false);
                            mLink.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    getHostServices().showDocument(ro.getLink());
                                }
                            });
                        } else {
                            mEmail.setVisible(false);
                            mLink.setVisible(false);
                            mGetToken.setVisible(false);
                            mGiveEmailTxt.setText("Niepoprawny adres email, spróbuj ponownie");
                        }
                    }
                });
            }
        });

        visibleErrorPopUp(false);

        mBtnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                RequestData mDataLogin = new RequestData(mUsername.getText(), mPassword.getText(), mDomain.getText());
                Gson json = new Gson();
                String jsonInputString = json.toJson(mDataLogin);
                RequestService rs = new RequestService();

                ResponseObject ro = null;
                try {
                    ro = rs.requestLoginSuccess(jsonInputString);

                    if(ro.isSuccess())
                    {
                        currAcc = new CurrentlyLoggedAccount(ro.getUser_id(), ro.getUsername(), ro.getPrivilege(), mDomain.getText());

                        if (ro.getPrivilege() != null && ro.getPrivilege().equals("Administrator") && mDataLogin.getDomain().equals(mDomain.getText()) && mDataLogin.getEmail().equals(mUsername.getText()) && mDataLogin.getPassword().equals(mPassword.getText())) {
                            logowaniePane(ADMIN_VIEW, "panel administratora", ro);
                            ((Node)(e.getSource())).getScene().getWindow().hide();
                        } else if (ro.getPrivilege() != null && ro.getPrivilege().equals("Kierownik") && mDataLogin.getDomain().equals(mDomain.getText()) && mDataLogin.getEmail().equals(mUsername.getText()) && mDataLogin.getPassword().equals(mPassword.getText())) {
                            logowaniePane(BOSS_VIEW, "panel kierownika", ro);
                            ((Node)(e.getSource())).getScene().getWindow().hide();
                        } else if (ro.getPrivilege() != null && ro.getPrivilege().equals("Pracownik") && mDataLogin.getDomain().equals(mDomain.getText()) && mDataLogin.getEmail().equals(mUsername.getText()) && mDataLogin.getPassword().equals(mPassword.getText())) {
                            logowaniePane(USER_VIEW, "panel użytkownika", ro);
                            ((Node)(e.getSource())).getScene().getWindow().hide();
                        } else {
                            visibleErrorPopUp(true);
                            mBtnClose.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    visibleErrorPopUp(false);
                                }
                            });
                        }
                    } else if (!ro.isSuccess())
                        DialogsUtils.errorDialog("Błąd", "Błąd z serwera", ro.getMsg());
                } catch (IOException ex) {
                    DialogsUtils.shortErrorDialog("Błąd", "Nie można się zalogować. Błąd połączenia z serwerem.");
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {}
}