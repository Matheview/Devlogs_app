package controllers;

import backend.requestObjects.RqNewProject;
import backend.responseObjects.*;
import backend.RequestService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.DialogsUtils;

import java.awt.event.InputMethodEvent;

public class BossController extends BaseController {

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
    private ComboBox<Domain> mChooseWorkspace;

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
        refrashProjectsList();

        RsDomains domains = requestService.getUserDomains(Controller.currAcc.getUser_id());
        mChooseWorkspace.getItems().addAll(domains.getDomains());

    }

    public void refrashProjectsList() {
        RequestService requestService = new RequestService();

        RsProjects projects = requestService.getUserProjects(Controller.currAcc.getUser_id());
        mProjectsList.getItems().clear();
        mProjectsList.getItems().addAll(projects.getProjects());
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
        Controller.currAcc = null;
        getController().clearFields();
        getController().showWindow();
        // get a handle to the stage
        Stage stage = (Stage) mLogoutIcon.getScene().getWindow();
        // do what you have to do
        stage.close();
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

    @FXML
    public void addNewProjectActionEvetnt(ActionEvent actionEvent) {
        String project_name = mNewProjectInput.getText();
        if (!project_name.isEmpty()){
            int user_id = Controller.currAcc.getUser_id();
            int domain_id = mChooseWorkspace.getValue().getId();

            RequestService requestService = new RequestService();
            RqNewProject newProject = new RqNewProject(user_id, project_name, domain_id);
            RsProject response = requestService.createNewProject(newProject);

            if (response.getMsg().equals("Project name already exist inside this domain"))
                DialogsUtils.shortErrorDialog("Błąd", "Projekt o takiej nazwie już istnieje.");
            else if (response.isSuccess()) {
                DialogsUtils.infoDialog("Sukces", "Utworzono nowy projekt", "Utworzono nowy projekt o nazwie: " + response.getProject_name());
                refrashProjectsList();
            }
        } else {
            DialogsUtils.shortErrorDialog("Błąd", "Proszę podać nazwę projetu.");
        }
    }

    @FXML
    public void workspaceSelectedActionEvent(ActionEvent actionEvent) {
        mAddNewProject.setDisable(false);
    }
}
