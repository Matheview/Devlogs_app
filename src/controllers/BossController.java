package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.InputMethodEvent;

public class BossController {

    @FXML
    private AnchorPane mWrapper;

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

    @FXML
    private Pane mMain;

    @FXML
    private ListView<?> mProjectsList;

    @FXML
    private TextField mNewProjectInput;

    @FXML
    private MenuButton mChooseWorkspace;

    @FXML
    private Button mAddNewProject;

    @FXML
    private ImageView mCloseNotificationPanelIcon;

    @FXML
    private ListView<?> mNotificationsList;

    @FXML
    private Pane mInProjectContainer;

    @FXML
    private Pane mAddNewTask;

    @FXML
    void backToHome(MouseEvent event) {

    }

    @FXML
    void checkNewProjectInputValue(InputMethodEvent event) {

    }

    @FXML
    void closeNotificationsPanel(MouseEvent event) {

    }

    @FXML
    void createNewProject(MouseEvent event) {

    }

    @FXML
    void logoutUser(MouseEvent event) {

    }

    @FXML
    void showNotificationPanel(MouseEvent event) {

    }

    @FXML
    void showNotificationsPanel(MouseEvent event) {

    }

    @FXML
    void showWorkspaces(MouseEvent event) {

    }

}
