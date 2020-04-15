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
    private TextField mProjectName;

    @FXML
    private Button mNewWorkspaceBtn;

    @FXML
    private TextField mProjectBoss;

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

}
