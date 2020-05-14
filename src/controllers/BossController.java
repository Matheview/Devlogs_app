package controllers;

import backend.requestObjects.RqNewProject;
import backend.responseObjects.*;
import backend.RequestService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.DialogsUtils;

import java.awt.event.InputMethodEvent;
import java.io.IOException;

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
    private Pane mNotificationsPanel;

    @FXML
    private Pane mInProjectContainer;

    @FXML
    private Pane mAddNewTask;


    @FXML
    private Pane mNewTaskPanel;

    @FXML
    private Label mTaskTitle;

    @FXML
    private CheckBox mLowPriority;

    @FXML
    private CheckBox mMediumPriority;

    @FXML
    private CheckBox mHighPriority;

    @FXML
    private Pane mUsersInProjectPanel;

    @FXML
    private ListView<?> mUsersInTaskList;

    @FXML
    private Pane mTask;

    @FXML
    private Label mTaskStartDate;

    @FXML
    private Label mTaskDeadline;

    @FXML
    private Pane mTaskPriority;

    @FXML
    private Label mTaskCommentsCount;

    @FXML
    private Pane mCommentsPanel;

    @FXML
    private Label mTaskTitleInCommentsPanel;

    @FXML
    private TextArea mTaskDescription;

    @FXML
    private ListView<?> mTaskComments;

    @FXML
    private TextField mMyComment;

    @FXML
    private Pane mInvitationPanel;

    @FXML
    private ListView<?> mUsersInProjectList;

    @FXML
    private TextField mInvitationInput;

    @FXML
    private TextField mInputTaskTitle;

    @FXML
    private Label mProjectTitle;

    @FXML
    private Pane mProjectNavbar;





    //Views initialize
    public void initialize() {
        // TODO  tu sobie włączyłem widok do danego projektu, żeby go wyłączyć klikasz domek
        mInProjectContainer.setVisible(true);

        mWelcomeUserName.setText(Controller.currAcc.getUsername());
        mPrivilegeUser.setText(Controller.currAcc.getPrivilege());

        refresh();
    }

    /**
     * Metoda odświeżająca listy projektów i domen
     */
    @Override
    public void refresh() {
        RequestService requestService = new RequestService();

        RsProjects projects;
        try {
            projects = requestService.getUserProjects(getUserId());
            mProjectsList.getItems().clear();
            mProjectsList.getItems().addAll(projects.getProjects());

            // funkcja nadająca elementom listy klasę css
            mProjectsList.setCellFactory(lv -> new ListCell<Project>() {
                @Override
                protected void updateItem(Project project, boolean empty) {
                    super.updateItem(project, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(project.toString());

                        // Funkcja nasłuchująca, jaki projekt na liście został kliknięty. Otiera panel z informacjami o projekcie
                        setOnMouseClicked(new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(MouseEvent event) {
                                mInProjectContainer.setVisible(true);
                            }
                        });
                    }
                    // nadanie elementowi listy klasy css
                    getStyleClass().add("project-list-item");
                }
            });
        } catch (IOException e) {
            DialogsUtils.shortErrorDialog("Błąd", "Nie można pobrać listy projektów z serwera. Błąd połączenia z serwerem.");
            e.printStackTrace();
        }

        RsDomains domains;
        try {
            domains = requestService.getUserDomains(Controller.currAcc.getUser_id());
            mChooseWorkspace.getItems().clear();
            mChooseWorkspace.getItems().addAll(domains.getDomains());
        } catch (IOException e) {
            DialogsUtils.shortErrorDialog("Błąd", "Nie można pobrać listy domen z serwera. Błąd połączenia z serwerem.");
            e.printStackTrace();
        }
    }

    //TODO trzeba zrobić coś takiego, że jak klikasz na dany projekt w liście to pojawia się widok projektu i zmienia się mNavbar.setVisible na false, a robi się mProjectNavbar.setVisible(true)


    @FXML //TODO domkiem się cofasz do ekranu kierownika (co ty nie powiesz) :)
    void backToHome(MouseEvent event) {
        mInProjectContainer.setVisible(false);
        mProjectNavbar.setVisible(false);
        mNavbar.setVisible(true);
    }

    @FXML
    void checkNewProjectInputValue(InputMethodEvent event) {

    }

    @FXML
    void closeNotificationsPanel(MouseEvent event) {
        mNotificationsPanel.setVisible(false);

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
    mNotificationsPanel.setVisible(true);
    }

    @FXML
    void showNotificationsPanel(MouseEvent event) {
    mNotificationsPanel.setVisible(true);
    }

    @FXML
    void showWorkspaces(MouseEvent event) {

    }

    @FXML
    public void checkNewProjectInputValue(javafx.scene.input.InputMethodEvent inputMethodEvent) {

    }

    @FXML // TODO funckja do dodawania nowego komentarza
    void addNewComment(MouseEvent event) {

    }

    @FXML // TODO funckja do dodawania opisu
    void addNewDescription(MouseEvent event) {

    }

    @FXML // TODO funckja do dodawania nowego zadania
    void addNewTask(MouseEvent event) {

    }

    @FXML
    void closeInvitationPanel(MouseEvent event) {
        mInvitationPanel.setVisible(false);
    }

    @FXML
    void closeNewTaskPanel(MouseEvent event) {
        mNewTaskPanel.setVisible(false);
    }

    @FXML
    void closeUsersInProjectPanel(MouseEvent event) {
        mUsersInProjectPanel.setVisible(false);
    }

    @FXML // TODO funkcja do generowania pdfa, ale jak to zrobimy to nie mam pojęcia
    void generatePdf(MouseEvent event) {

    }

    @FXML // TODO funkcja sprawdzająca stan inputa komentarza jeśli bedzie potrzebna
    void handleCommentChange(InputMethodEvent event) {

    }

    @FXML // TODO funkcja sprawdzająca stan inputa do zaproszenia poprzez email / username jeśli bedzie potrzeba
    void handleInvitationInputChange(InputMethodEvent event) {

    }

    @FXML // TODO funkcja sprawdzająca stan inputa zmiany tytułu zadania jeśli będzie potrzebna
    void handleTitleTaskChange(InputMethodEvent event) {

    }

    @FXML
    void hideCommentsPanel(MouseEvent event) {
        mCommentsPanel.setVisible(false);
    }

    @FXML // TODO funckja wysyłająca zaproszenie do usera, pasuje to przełożyć na powiadomienia w panelu danego użytkownika, ale to już zabawa dla Kuby
    void sendInvitation(MouseEvent event) {

    }

    @FXML
    void showComments(MouseEvent event) {
        mCommentsPanel.setVisible(true);
    }

    @FXML
    void showInvitationPanel(MouseEvent event) {
        mInvitationPanel.setVisible(true);
    }

    @FXML
    void showNewTaskPanel(MouseEvent event) {
        mNewTaskPanel.setVisible(true);
    }

    @FXML
    void showUsersInProject(MouseEvent event) {
        mUsersInProjectPanel.setVisible(true);
    }

    @FXML
    public void handleInvitationInputChange(javafx.scene.input.InputMethodEvent inputMethodEvent) {
    }

    @FXML
    public void handleCommentChange(javafx.scene.input.InputMethodEvent inputMethodEvent) {
    }

    @FXML
    public void addNewProjectActionEvetnt(ActionEvent actionEvent) {
        String project_name = mNewProjectInput.getText();
        if (!project_name.isEmpty()){
            int user_id = getUserId();
            int domain_id = mChooseWorkspace.getValue().getId();

            RequestService requestService = new RequestService();
            RqNewProject newProject = new RqNewProject(user_id, project_name, domain_id);
            RsProject response;
            try {
                response = requestService.createNewProject(newProject);

                if (response.getMsg().equals("Project name already exist inside this domain"))
                    DialogsUtils.shortErrorDialog("Błąd", "Projekt o takiej nazwie już istnieje.");
                else if (response.isSuccess()) {
                    DialogsUtils.infoDialog("Sukces", "Utworzono nowy projekt", "Utworzono nowy projekt o nazwie: " + response.getProject_name());
                    refresh();
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można stworzyć nowego projektu. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        } else {
            DialogsUtils.shortErrorDialog("Błąd", "Proszę podać nazwę projetu.");
        }
    }

    @FXML
    public void workspaceSelectedActionEvent(ActionEvent actionEvent) {
        mAddNewProject.setDisable(false);
    }

    @FXML
    public void changeNameOfGroupTask(MouseEvent mouseEvent) {
        //TODO funckja, która ma pozwolić zmienić nazwę grupy zadać na np. Do zrobienia, Zrobione, W trakcie itd. , nie wiem czy to będzie więc to na końcu
    }

    @FXML
    public void handleTitleTaskChange(javafx.scene.input.InputMethodEvent inputMethodEvent) {
        //TODO funckja, sprawdzająca zawartość inputa do zmiany tytułu tasku, jeśli potrzebna
    }

    @FXML  // TODO funckja dodająca nową grupę tasków np. Do zrobienia, Robię itd trzeba dopytać mateusza czy to będzie czy na sztywno się ustawi grupy
    public void addNewGroupTask(MouseEvent mouseEvent) {

    }
}
