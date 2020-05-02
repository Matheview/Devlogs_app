package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class AdminController {

    //ZMIENNE DO OBSŁUŻENIA BACKEND

    @FXML
    private AnchorPane mWrapper;

    @FXML
    private Pane mMain;

    @FXML
    private TextField mUserName;

    @FXML
    private TextField mEmail;

    @FXML
    private PasswordField mPassword;

    @FXML
    private CheckBox mAdminType;

    @FXML
    private CheckBox mBossType;

    @FXML
    private CheckBox mUserType;

    @FXML
    private Button mNewUserBtn;

    @FXML
    private TextField mWorkspaceName;

    @FXML
    private Button mNewWorkspaceBtn;

    @FXML
    private TextField mWorkspaceSearch;

    @FXML
    private ListView<?> mWorkspaceList;

    @FXML
    private TextField mUserSearch;

    @FXML
    private ListView<?> mUserlist;

    @FXML
    private Pane mNavbar;

    @FXML
    private Label mWelcomeUserName;

    @FXML
    private ImageView mHomeIcon;

    @FXML
    private ImageView mNotificationsIcon;

    @FXML
    private ImageView mLogoutIcon;

    public void handleUserNameChange(InputMethodEvent inputMethodEvent) {
    }

    public void handleEmailChange(InputMethodEvent inputMethodEvent) {
    }

    public void handlePasswordChange(InputMethodEvent inputMethodEvent) {
    }

    public void handleAdminCheck(ActionEvent actionEvent) {
        System.out.println("elox");
    }

    public void handleBossCheck(ActionEvent actionEvent) {
    }

    public void handleUserCheck(ActionEvent actionEvent) {
    }

    public void makeNewUser(ActionEvent actionEvent) {
    }

    public void handleWorkspaceNameChange(InputMethodEvent inputMethodEvent) {
    }

    public void makeNewWorkspace(ActionEvent actionEvent) {
    }

    public void handleFindWorkspace(InputMethodEvent inputMethodEvent) {
    }

    public void handleFindUser(InputMethodEvent inputMethodEvent) {
    }
    //FUNKCJE DO OBSŁUŻENIA BACKEND
}
