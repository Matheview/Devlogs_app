package controllers;

import backend.requestObjects.RqNewProject;
import backend.requestObjects.RqStatus;
import backend.responseObjects.*;
import backend.RequestService;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.DialogsUtils;

import java.awt.event.InputMethodEvent;
import java.io.IOException;

public class BossController extends BaseController {

    /**
     * Projekt, którego szczegóły są obecnie wyświetlane
     */
    private RsProjectDetails activeProject;

    /**
     * Status, który został wybrany do np. edycji lub usunięcia
     */
    private Status selectedStatus;

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
    private ScrollPane mInProjectContainer;

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
    private ListView<User> mUsersInProjectList;

    @FXML
    private TextField mInvitationInput;

    @FXML
    private TextField mInputTaskTitle;

    @FXML
    private Label mProjectTitle;

    @FXML
    private Pane mProjectNavbar;

    @FXML
    private HBox mStatusesList;

    @FXML
    public Pane mInfoPanel;

    @FXML
    public ImageView mInfoIcon;

    @FXML
    public Label mTextInfoPanel;

    @FXML
    public Button mCloseInfoButton;

    @FXML
    public ImageView mCLoseInfoPanelIcon;

    @FXML
    public Pane mNotificationsCircle;

    @FXML
    public Label mNotificationsCounter;

    @FXML
    public Pane mNewStatusPane;

    @FXML
    public TextField mNewStatusName;

    @FXML
    public Pane mEditStatusPane;

    @FXML
    public TextField mEditStatusNameTextField;

    @FXML
    public Pane mDeleteStatusPane;

    @FXML
    public Label mDeleteStatusName;

    @FXML
    public ListView<User> mUsersInDomainList;

    // ------- panel raportów -------

    @FXML
    private Pane mPdfGeneratorPanel;

    @FXML
    private Label mRaportUserCounter; // licznik osób w projekcie

    @FXML
    private Label mRaportTaskCounter; // licznik zadań w projekcie

    @FXML
    private PieChart mRaportPieChart; // zmienna do wykresu kołowego pokazująca podział zadań ze względu na status : https://docs.oracle.com/javafx/2/charts/pie-chart.htm

    @FXML
    private ProgressBar mRaportProgressBar; // zmienna do progress bara ukazującego wizualny stan czasu ( dni) pozostałych do końca projektu, pasuje wyliczać na podstawie daty zaczęcia, zakończenia oraz dnia dzisiejszego ile jeszcze dni pozostało i przypisaywać wartość : https://docs.oracle.com/javafx/2/ui_controls/progress.htm

    @FXML
    private Label mRaportTaskStartDate; // zmienna do daty rozpoczęcia

    @FXML
    private Label mRaportTaskEndDate; // zmienna do daty zakończenia

    @FXML
    private Label mTimeTotheEnd; // zmienna do przechowywania info ile dni zostało

    @FXML
    private Label mRaportCurrentDate; // zmienna do daty dzisiejszej , wszystkie daty generować do foramtu dzień/miesiąc/rok

    @FXML
    private Pane mAvailableUsersPanel; // panel z dostępnymi userami w domenie do dodania do projektu

    //Views initialize
    public void initialize() {
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
            mProjectsList.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Project project, boolean empty) {
                    super.updateItem(project, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(project.toString());

                        // Funkcja nasłuchująca, jaki projekt na liście został kliknięty. Otiera panel z informacjami o projekcie
                        setOnMouseClicked(event -> {
                            showProjectDetails();

                            refreshProjectDetails(getSelectedProject());
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

    /**
     * Metoda zwracająca aktualnie wybrany projekt na liście
     */
    public Project getSelectedProject() {
        return mProjectsList.getSelectionModel().getSelectedItem();
    }

    /**
     * Pokaż panel ze szczegółami projektu
     */
    public void showProjectDetails() {
        mInProjectContainer.setVisible(true);
        mProjectNavbar.setVisible(true);
        mNavbar.setVisible(false);
        mMain.setVisible(false);
    }

    /**
     * Ukryj panel ze szczegółami projektu
     */
    public void hideProjectDetails() {
        closeAllPanels();
        mInProjectContainer.setVisible(false);
        mProjectNavbar.setVisible(false);
        mNavbar.setVisible(true);
        mMain.setVisible(true);
    }

    /**
     * Pokaż panel do tworzenia nowego statusu
     */
    public void showNewStatusPane() {
        closeAllPanels();
        mInProjectContainer.setDisable(true);
        mNewStatusPane.setVisible(true);
    }

    /**
     * Metoda zamykająca okno do tworzenia nowego statusu
     */
    public void closeNewStatusPane() {
        mInProjectContainer.setDisable(false);
        mNewStatusPane.setVisible(false);
        mNewStatusName.clear();
    }

    /**
     * Pokaż panel do edycji nazwy statusu
     */
    public void showEditStatusPane() {
        closeAllPanels();
        mInProjectContainer.setDisable(true);

        mEditStatusNameTextField.setText(selectedStatus.getName());
        mEditStatusPane.setVisible(true);
    }

    /**
     * Metoda zamykająca okno do zmiany nazwy statusu
     */
    public void closeEditStatusPane() {
        mInProjectContainer.setDisable(false);
        mEditStatusPane.setVisible(false);
        mEditStatusNameTextField.clear();
    }

    /**
     * Pokaż panel do usuwania nazwy statusu
     */
    public void showDeleteStatusPane() {
        closeAllPanels();
        mInProjectContainer.setDisable(true);

        mDeleteStatusName.setText(selectedStatus.getName());
        mDeleteStatusPane.setVisible(true);
    }

    /**
     * Metoda zamykająca okno do usuwania statusu
     */
    public void closeDeleteStatusPane() {
        mInProjectContainer.setDisable(false);
        mDeleteStatusPane.setVisible(false);
        mDeleteStatusName.setText("");
    }

    /**
     * Funkcja zamykająca wszystkie pomniejsze panele
     */
    public void closeAllPanels() {
        closeNewStatusPane();
        closeEditStatusPane();
        closeDeleteStatusPane();
        mAvailableUsersPanel.setVisible(false);
        mInvitationPanel.setVisible(false);
        mCommentsPanel.setVisible(false);
    }

    /**
     * Funkcja wyświetlająca powiadomienie z informacją
     */
    public void showInfoPanel(String message) {
        mInfoPanel.setVisible(true);

        mInfoIcon.setImage(new Image("/imgs/info.png"));

        mTextInfoPanel.getStyleClass().clear();
        mTextInfoPanel.getStyleClass().add("info-panel-text");
        mTextInfoPanel.setText(message);

        mCloseInfoButton.getStyleClass().clear();
        mCloseInfoButton.getStyleClass().add("creator-btn");

        mCLoseInfoPanelIcon.setImage(new Image("/imgs/close.png"));
    }

    /**
     * Funkcja wyświetlająca powiadomienie o błędzie
     */
    public void showErrorPanel(String message) {
        mInfoPanel.setVisible(true);

        mInfoIcon.setImage(new Image("/imgs/warn.png"));

        mTextInfoPanel.getStyleClass().clear();
        mTextInfoPanel.getStyleClass().add("error-panel-text");
        mTextInfoPanel.setText(message);

        mCloseInfoButton.getStyleClass().clear();
        mCloseInfoButton.getStyleClass().add("error-btn");

        mCLoseInfoPanelIcon.setImage(new Image("/imgs/close-red.png"));
    }

    /**
     * Funkcja odświeżająca panel ze szczegółami projektu
     * @param project projekt do załadowania w panelu
     */
    public void refreshProjectDetails(Project project) {
        if (project != null) {
            RequestService requestService = new RequestService();

            int project_id = project.getId();

            RsProjectDetails response;
            try {
                response = requestService.getProjectDetails(project_id);

                if (response.isSuccess()) {
                    mStatusesList.getChildren().clear();
                    activeProject = response;

                    mProjectTitle.setText(activeProject.getProject_name());

                    for (Status status : activeProject.getStatuses()) {

                        // VBox przechowujący zadania z danego statusu
                        VBox vBox = getVBox();

                        // Tytuł statusu
                        Pane statusName = getStatusTitlePane(status.getName(), "/imgs/edit.png");
                        statusName.setUserData(status);
                        vBox.getChildren().add(statusName);

                        for (Task task : status.getTasks()) {
                            // Panel z informacjami o tasku
                            Pane taskPane = getTaskPane(task);
                            vBox.getChildren().add(taskPane);
                        }

                        Pane addNewTask = getNewTaskPane();
                        vBox.getChildren().add(addNewTask);

                        mStatusesList.getChildren().add(vBox);
                    }

                    // ostatni VBox z przuciskiem do dodawania nowego statusu
                    VBox vBox = getVBox();
                    Pane addStatus = getStatusTitlePane("nowy status", "/imgs/addtaskwhite.png");
                    addStatus.getStyleClass().add("hover-hand-cursor");
                    addStatus.setOnMouseClicked(this::addNewGroupTask);
                    vBox.getChildren().add(addStatus);
                    mStatusesList.getChildren().add(vBox);

                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można pobrać szczegułów projekcie. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda zwracająca standardowy dla tej aplikacji VBox
     * @return standardoty VBox
     */
    private VBox getVBox() {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.TOP_CENTER);

        return vBox;
    }

    /**
     * Metoda zwracajjąca panel zawierający nazwę statusu
     * @param title nazwa statusu
     * @return panel z tytułem statusu
     */
    private Pane getStatusTitlePane(String title, String icon) {
        // Panel statusu
        Pane pane = new Pane();
        pane.getStyleClass().add("tasks-list");

        pane.setPadding(new Insets(5, 5, 5, 5));

        // Ikona
        ImageView editIcon = new ImageView(new Image(icon));
        editIcon.setFitWidth(16.0);
        editIcon.setFitHeight(16.0);
        editIcon.setLayoutX(20.0);
        editIcon.setLayoutY(12.0);
        pane.getChildren().add(editIcon);

        // Jeśli jest to ikona ołówka
        if (icon.equals("/imgs/edit.png")) {
            editIcon.getStyleClass().add("hover-hand-cursor");
            // Metoda wyświetlająca panel do zmiany nazwy
            editIcon.setOnMouseClicked(event -> {
                selectedStatus = (Status) getParentData(event);
                showEditStatusPane();
            });


            // Ikona śmietnika
            ImageView trashIcon = new ImageView(new Image("/imgs/trash.png"));
            trashIcon.setFitWidth(20.0);
            trashIcon.setFitHeight(24.0);
            trashIcon.setLayoutX(263.0);
            trashIcon.setLayoutY(8.0);
            trashIcon.getStyleClass().add("hover-hand-cursor");
            // Metoda wyświetlająca panel do usówania statusu
            trashIcon.setOnMouseClicked(event -> {
                selectedStatus = (Status) getParentData(event);
                showDeleteStatusPane();
            });
            pane.getChildren().add(trashIcon);
        }

        // Tytuł statusu
        Label titleLabel = new Label(title);
        titleLabel.setLayoutX(47.0);
        titleLabel.setLayoutY(5.0);
        titleLabel.getStyleClass().add("topic-name");
        pane.getChildren().add(titleLabel);

        return pane;
    }

    /**
     * Metode zwracajjąca panel zawierający dane o statusie
     * @param task obiekt taska
     * @return panel z informacjami o tasku
     */
    private Pane getTaskPane(Task task) {
        // Panel taska
        Pane pane = new Pane();
        pane.getStyleClass().add("task-view");
        pane.setPadding(new Insets(10, 10, 10, 10));

        // Tytuł taska
        Label taskTitle = new Label(task.getName());
        taskTitle.setLayoutX(14.0);
        taskTitle.setLayoutY(7.0);
        taskTitle.getStyleClass().add("task-title");
        pane.getChildren().add(taskTitle);

        // Panel z priorytetem
        Pane priority = new Pane();
        priority.setLayoutX(281.0);
        priority.setLayoutY(9.0);
        priority.getStyleClass().add("priority");
        pane.getChildren().add(priority);

        // Panel ze skrótem nazwy przydzielonego do taska pracownika
        if (task.getGranted_to() != null) {
            User user = null;

            for (User u : activeProject.getUsers()) {
                if (u.getId() == task.getGranted_to()) {
                    user = u;
                    break;
                }
            }

            if (user != null) {
                Label userShortcut = new Label(user.getShortcut());
                userShortcut.setAlignment(Pos.CENTER);
                userShortcut.setLayoutX(13.0);
                userShortcut.setLayoutY(35.0);
                userShortcut.getStyleClass().add("user-circle");
                pane.getChildren().add(userShortcut);
            }
        }

        // Data utworzenia
        //Label startDate = new Label(task.getCreated_at());
        //startDate.setLayoutX(14.0);
        //startDate.setLayoutY(74.0);
        //startDate.getStyleClass().add("start-date");
        //pane.getChildren().add(startDate);

        // Deadline
        if (task.getDeadline() != null) {
            Label deadline = new Label(task.getDeadline());
            deadline.setLayoutX(14.0);
            deadline.setLayoutY(74.0);
            deadline.getStyleClass().add("deadline");
            pane.getChildren().add(deadline);
        }

        // Liczba komentarzy
        Label commentsCount = new Label(Integer.toString(task.getComments_count()));
        commentsCount.setAlignment(Pos.CENTER);
        commentsCount.setLayoutX(233.0);
        commentsCount.setLayoutY(74.0);
        commentsCount.getStyleClass().add("comments-count");
        pane.getChildren().add(commentsCount);

        // Ikona komentarza
        ImageView editIcon = new ImageView(new Image("/imgs/cloud.png"));
        editIcon.setFitWidth(30.0);
        editIcon.setFitHeight(30.0);
        editIcon.setLayoutX(253.0);
        editIcon.setLayoutY(54.0);
        pane.getChildren().add(editIcon);

        return pane;
    }

    /**
     * Metode zwracajjąca panel zawierający przycisk do tworzenia nowego taska
     * @return panel "dodaj nowe zadanie"
     */
    private Pane getNewTaskPane() {
        // Panel taska
        Pane pane = new Pane();
        pane.getStyleClass().add("add-new-task");
        pane.setMaxWidth(200.0);
        pane.setPadding(new Insets(25, 25, 25, 25));

        // Ikona
        ImageView editIcon = new ImageView(new Image("/imgs/addtask.png"));
        editIcon.setFitWidth(20.0);
        editIcon.setFitHeight(20.0);
        editIcon.setLayoutX(24.0);
        editIcon.setLayoutY(14.0);
        pane.getChildren().add(editIcon);

        // "Dodaj nowe zadanie"
        Label addTaskLabel = new Label("dodaj nowe zadanie");
        addTaskLabel.setLayoutX(47.0);
        addTaskLabel.setLayoutY(12.0);
        addTaskLabel.getStyleClass().add("new-task-add-label");
        pane.getChildren().add(addTaskLabel);

        return pane;
    }

    private Object getParentData(Event event) {
        return ( (Node)event.getSource() ).getParent().getUserData();
    }

    @FXML
    void backToHome(MouseEvent event) {
        hideProjectDetails();
    }

    @FXML
    void checkNewProjectInputValue(InputMethodEvent event) {}

    @FXML
    void closeNotificationsPanel(MouseEvent event) {
        mNotificationsPanel.setVisible(false);
    }

    @FXML
    void createNewProject(MouseEvent event) {}

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
    void showWorkspaces(MouseEvent event) {}

    @FXML
    public void checkNewProjectInputValue(javafx.scene.input.InputMethodEvent inputMethodEvent) {}

    @FXML // TODO funckja do dodawania nowego komentarza
    void addNewComment(MouseEvent event) {}

    @FXML // TODO funckja do dodawania opisu
    void addNewDescription(MouseEvent event) {}

    @FXML // TODO funckja do dodawania nowego zadania
    void addNewTask(MouseEvent event) {}

    @FXML
    void closeNewTaskPanel(MouseEvent event) {
        mNewTaskPanel.setVisible(false);
    }

    @FXML
    void closeUsersInProjectPanel(MouseEvent event) {
        mUsersInProjectPanel.setVisible(false);
    }

    @FXML // TODO funkcja do generowania pdfa, ale jak to zrobimy to nie mam pojęcia
    void generatePdf(MouseEvent event) {}

    @FXML // TODO funkcja sprawdzająca stan inputa komentarza jeśli bedzie potrzebna
    void handleCommentChange(InputMethodEvent event) {}

    @FXML // TODO funkcja sprawdzająca stan inputa do zaproszenia poprzez email / username jeśli bedzie potrzeba
    void handleInvitationInputChange(InputMethodEvent event) {}

    @FXML // TODO funkcja sprawdzająca stan inputa zmiany tytułu zadania jeśli będzie potrzebna
    void handleTitleTaskChange(InputMethodEvent event) {}

    @FXML
    void hideCommentsPanel(MouseEvent event) {
        mCommentsPanel.setVisible(false);
    }

    @FXML
    void sendInvitation(MouseEvent event) {}

    @FXML
    void showComments(MouseEvent event) {
        mCommentsPanel.setVisible(true);
    }

    @FXML
    void showInvitationPanel(MouseEvent event) {
        mInvitationPanel.setVisible(true);

        mUsersInProjectList.getItems().clear();
        mUsersInProjectList.getItems().addAll(activeProject.getUsers());
    }

    @FXML
    void closeInvitationPanel(MouseEvent event) {
        mAvailableUsersPanel.setVisible(false);
        mInvitationPanel.setVisible(false);
    }

    @FXML
    public void showAvailableUsersPanel(MouseEvent mouseEvent) {
        mAvailableUsersPanel.setVisible(true);

        RequestService requestService = new RequestService();

        RsUsersInDomain responseObject;
        try {
            responseObject = requestService.getUsersFromDomain(getUserId(), getDomain());
            mUsersInDomainList.getItems().clear();
            mUsersInDomainList.getItems().addAll(responseObject.getUsers());

        } catch (IOException e) {
            DialogsUtils.shortErrorDialog("Błąd", "Nie można pobrać listy użytkowników z serwera. Błąd połączenia z serwerem.");
            e.printStackTrace();
        }
    }

    @FXML
    public void closeAvailableUsers(MouseEvent mouseEvent) {
        mAvailableUsersPanel.setVisible(false);
    }

    @FXML  // Funkcja do dodawania użytkownika do projektu
    public void addUserToProject(ActionEvent actionEvent) {
        
    }

    @FXML  // Funkcja do usuwania użytkownika z projektu
    public void deleteUserFromProject(MouseEvent mouseEvent) {

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
    public void handleInvitationInputChange(javafx.scene.input.InputMethodEvent inputMethodEvent) {}

    @FXML
    public void handleCommentChange(javafx.scene.input.InputMethodEvent inputMethodEvent) {}

    @FXML
    public void addNewProjectActionEvent(ActionEvent actionEvent) {
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
    public void changeNameOfGroupTask(MouseEvent mouseEvent) {}

    @FXML
    public void handleTitleTaskChange(javafx.scene.input.InputMethodEvent inputMethodEvent) {
        //TODO funckja, sprawdzająca zawartość inputa do zmiany tytułu tasku, jeśli potrzebna
    }

    @FXML
    public void addNewGroupTask(MouseEvent mouseEvent) {
        showNewStatusPane();
    }

    @FXML
    void showPdfGeneratorPanel(MouseEvent event) {
        mPdfGeneratorPanel.setVisible(true);
        // TODO tu jeszcze trzeba funkcję, która zmieni zawartość progres bara w zaleźności od ilości dni pozostałych do końca wykonania projektu
    }

    @FXML
    void closePdfGeneratorPanel(MouseEvent event) {
        mPdfGeneratorPanel.setVisible(false);
    }

    @FXML
    public void closeInfoPanel(MouseEvent event) {
        mInfoPanel.setVisible(false);
    }

    @FXML
    public void acceptInfoPanel(MouseEvent event) {
        mInfoPanel.setVisible(false);
    }

    @FXML  // Funkcja zamykająca panel do tworzenia nowego statusu
    public void closeNewStatusPaneHandler(MouseEvent mouseEvent) {
        closeNewStatusPane();
    }

    @FXML
    public void closeEditStatusPaneHandler(MouseEvent mouseEvent) {
        closeEditStatusPane();
    }

    @FXML
    public void closeDeleteStatusPaneHandler(MouseEvent mouseEvent) {
        closeDeleteStatusPane();
    }

    @FXML
    public void cancelDeleteStatusPaneHandler(ActionEvent actionEvent) {
        closeDeleteStatusPane();
    }

    @FXML
    public void createNewStatus(ActionEvent actionEvent) {
        String domain = getDomain();
        int project_id = activeProject.getProject_id();
        String status_desc = mNewStatusName.getText();
        int user_id = getUserId();

        if (status_desc.isEmpty())
            showErrorPanel("Błąd: Proszę podać nazwę nowego statusu.");
        else {
            RequestService requestService = new RequestService();
            RqStatus requestObject = new RqStatus(domain, project_id, status_desc, user_id);

            RsStatus response;
            try {
                response = requestService.createNewStatus(requestObject);

                if (response.getMsg().equals("Status description already exists"))
                    showErrorPanel("Błąd: Status o takiej nazwie już istnieje.");
                else if (response.isSuccess()) {
                    showInfoPanel("Sukces: Utworzono nowy status o nazwie: " + status_desc + ".");
                    closeNewStatusPane();
                    refreshProjectDetails(getSelectedProject());
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można stworzyć nowego statusu. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void editStatus(ActionEvent actionEvent) {
        String domain = getDomain();
        int project_id = activeProject.getProject_id();
        int status_id = selectedStatus.getStatus_id();
        String status_desc = mEditStatusNameTextField.getText();
        int user_id = getUserId();

        if (status_desc.isEmpty())
            showErrorPanel("Błąd: Proszę podać nową nazwę statusu.");
        else if ( status_desc.equals( selectedStatus.getName() ) ) {
            closeEditStatusPane();
            refreshProjectDetails(getSelectedProject());
        } else {
            RequestService requestService = new RequestService();
            RqStatus requestObject = new RqStatus(domain, project_id, status_id, status_desc, user_id);

            BaseResponseObject response;
            try {
                response = requestService.editStatus(requestObject);

                if (response.getMsg().equals("Status description already exists"))
                    showErrorPanel("Błąd: Status o takiej nazwie już istnieje.");
                else if (response.getMsg().equals("Status description not changed"))
                    showInfoPanel("Ktoś już zmienił nazwę tego statusu na: " + status_desc + ".");
                else if (response.isSuccess()) {
                    showInfoPanel("Sukces: Zmieniono nazwę statusu na: " + status_desc + ".");
                    closeEditStatusPane();
                    refreshProjectDetails(getSelectedProject());
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można zmienić nazwy statusu. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void deleteStatus(ActionEvent actionEvent) {
        if ( selectedStatus != null ) {
            String domain = getDomain();
            int project_id = activeProject.getProject_id();
            int status_id = selectedStatus.getStatus_id();
            int user_id = getUserId();

            RequestService requestService = new RequestService();
            RqStatus requestObject = new RqStatus(domain, project_id, status_id, user_id);

            BaseResponseObject response;
            try {
                response = requestService.deleteStatus(requestObject);

                /*if (response.getMsg().equals("Status description already exists"))
                    showErrorPanel("Błąd: Status o takiej nazwie już istnieje.");
                else */if (response.isSuccess()) {
                    showInfoPanel("Usunięto status: " + selectedStatus.getName() + ".");
                    closeDeleteStatusPane();
                    refreshProjectDetails(getSelectedProject());
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można usunąć statusu. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        }
    }

}
