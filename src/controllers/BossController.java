package controllers;

import backend.Domain;
import backend.RequestService;
import backend.responseObjects.Project;
import backend.responseObjects.RsDomains;
import backend.responseObjects.RsProjects;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;

import java.awt.event.InputMethodEvent;

public class BossController extends BaseController {

    ObservableList<Project> olProjects;

    @FXML
    private AnchorPane mWrapper;

    @FXML
    private Pane mNavbar;

    @FXML
    private Label mWelcomeUserName;

    @FXML
    private Label mPrivilegeUser;

    @FXML
    private ImageView mHomeIcon;

    @FXML
    private ImageView mNotificationsIcon;

    @FXML
    private ImageView mLogoutIcon;

    @FXML
    private Pane mMain;

    @FXML
    private ListView<Project> mProjectsList;

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


    //Views initialize
    public void initialize() {
        mWelcomeUserName.setText(Controller.currAcc.getUsername());
        mPrivilegeUser.setText(Controller.currAcc.getPrivilege());

        RequestService requestService = new RequestService();

        RsProjects projects = requestService.getUserProjects(Controller.currAcc.getUser_id());
        mProjectsList.getItems().addAll(projects.getProjects());

        RsDomains domains = requestService.getUserDomains(Controller.currAcc.getUser_id());
        for (Domain domain : domains.getDomains()) {
            mChooseWorkspace.getItems().add(new MenuItem(domain.toString()));
        }
    }

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

    @FXML
    public void checkNewProjectInputValue(javafx.scene.input.InputMethodEvent inputMethodEvent) {

    }
}
