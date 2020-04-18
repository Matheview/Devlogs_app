package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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

    //FUNKCJE DO OBSŁUŻENIA BACKEND

    @FXML
    void handleAdminCheck(ActionEvent event) {

    }

    @FXML
    void handleBossCheck(ActionEvent event) {

    }

    @FXML
    void handleEmailChange(InputMethodEvent event) {

    }

    @FXML
    void handleFindUser(InputMethodEvent event) {

    }

    @FXML
    void handleFindWorkspace(InputMethodEvent event) {

    }

    @FXML
    void handlePasswordChange(InputMethodEvent event) {

    }

    @FXML
    void handleUserCheck(ActionEvent event) {

    }

    @FXML
    void handleUserNameChange(InputMethodEvent event) {

    }

    @FXML
    void handleWorkspaceNameChange(InputMethodEvent event) {

    }

    @FXML
    void makeNewUser(ActionEvent event) {

    }

    @FXML
    void makeNewWorkspace(ActionEvent event) {

    }

}
