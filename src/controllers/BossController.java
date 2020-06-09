package controllers;

import backend.requestObjects.RqNewProject;
import backend.requestObjects.RqTask;
import backend.requestObjects.RqStatus;
import backend.requestObjects.RqUser;
import backend.responseObjects.*;
import backend.RequestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import java.util.Collections;
import javafx.scene.web.WebView;
import utils.PdfGenerator;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Klasa bazowa dla wszystkich kontrolerów. Zawiera wszystkie elementy wspólne
 */
public class BossController extends BaseController {

    /**
     * Projekt, którego szczegóły są obecnie wyświetlane
     */
    private RsProjectDetails activeProject;

    /**
     * Status, który został wybrany do np. edycji lub usunięcia
     */
    private Status selectedStatus;

    /**
     * Task, który został wybrany do np. edycji lub usunięcia
     */
    private Task selectedTask;

    /**
     * Task, którego szczegóły są obecnie wyświetlane (zawiera więcej informacji niż selectedTask)
     * Pobierany jest w momencie użycia metody refreshTaskDetails()
     */
    private RsTaskDetails activeTask;

    /**
     * Aktywny panel do dodawania nowego taska
     */
    private Pane activeAddNewTaskPane;

    /**
     * Aktywny panel do dodawania nowego użytkownika do taska
     */
    private Pane activeAddNewUserToTaskPane;

    /**
     * Zmienna przechowująca referencję do ostatniego rodzica panelu taska
     */
    private VBox lastParent;

    /**
     * Zmienna przechowująca referencję do następnego rodzica panelu taska
     */
    private VBox nextParent;

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
    private TextField mTaskTitleInCommentsPanel;

    @FXML
    private TextArea mTaskDescription;

    @FXML
    private VBox mTaskComments;

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
    private Pane mRootPane;

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

    @FXML
    public Pane mDeleteUserPane;

    @FXML
    public Label mDeleteUserName;

    @FXML
    protected Label mUserInTaskDetails;

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
    @Override
    public void initialize() {
        super.initialize();
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
                    getStyleClass().add("hover-hand-cursor");
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
     * Metoda zwracająca aktualnie wybranego użytkownika na liście użytkowników w projekcie
     */
    public User getSelectedUserInProjectList() {
        return mUsersInProjectList.getSelectionModel().getSelectedItem();
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
        disableProjectPane();
        mNewStatusPane.setVisible(true);
    }

    /**
     * Metoda zamykająca okno do tworzenia nowego statusu
     */
    public void closeNewStatusPane() {
        ableProjectPane();
        mNewStatusPane.setVisible(false);
        mNewStatusName.clear();
    }

    /**
     * Pokaż panel do edycji nazwy statusu
     */
    public void showEditStatusPane() {
        closeAllPanels();
        disableProjectPane();

        mEditStatusNameTextField.setText(selectedStatus.getName());
        mEditStatusPane.setVisible(true);
    }

    /**
     * Metoda zamykająca okno do zmiany nazwy statusu
     */
    public void closeEditStatusPane() {
        ableProjectPane();
        mEditStatusPane.setVisible(false);
        mEditStatusNameTextField.clear();
    }

    /**
     * Pokaż panel do usuwania statusu
     */
    public void showDeleteStatusPane() {
        closeAllPanels();
        disableProjectPane();

        mDeleteStatusName.setText(selectedStatus.getName());
        mDeleteStatusPane.setVisible(true);
    }

    /**
     * Metoda zamykająca okno do usuwania statusu
     */
    public void closeDeleteStatusPane() {
        ableProjectPane();
        mDeleteStatusPane.setVisible(false);
        mDeleteStatusName.setText("");
    }

    /**
     * Pokaż panel do usuwania użytkownika z projektu
     */
    public void showDeleteUsersPane() {
        disableProjectPane();

        mDeleteUserName.setText(getSelectedUserInProjectList().getName());
        mDeleteUserPane.setVisible(true);
    }

    /**
     * Metoda zamykająca okno do usuwania użytkownika z projektu
     */
    public void closeDeleteUserPane() {
        ableProjectPane();
        mDeleteUserPane.setVisible(false);
        mDeleteUserName.setText("");
    }

    /**
     * Funkcja do wyłączania panelu ze szczegółami projektu (nie mylić z setVisible(false))
     */
    public void disableProjectPane() {
        mInProjectContainer.setDisable(true);
        mProjectNavbar.setDisable(true);
    }

    /**
     * Funkcja do włączania panelu ze szczegółami projektu (nie mylić z setVisible(true))
     */
    public void ableProjectPane() {
        mInProjectContainer.setDisable(false);
        mProjectNavbar.setDisable(false);
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
        closeTaskDescriptionPanel();
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
                        vBox.setUserData(status);
                        vBox.getChildren().add(statusName);

                        for (Task task : status.getTasks()) {
                            // Panel z informacjami o tasku
                            Pane taskPane = getTaskPane(task);

                            // Funkcje do obsługi Drag and Drop po stronie elementu przeciąganego
                            // jeśli klawisz myszy został przyciśnięty
                            taskPane.setOnMousePressed(event -> {
                                // Sprawdzenie, czy kliknięto w obrębie taskPane, a nie w obrębie jego "dziecka"
                                // ma to zapobiegać używania tej funkcji w momencie, gdy klikniemy w np. label "tytół taska"
                                // (wtedy zostanie użyta funkcja przypisana właśnie do elementu label, a nie ta)
                                if (event.getTarget() == taskPane) {
                                    taskPane.setMouseTransparent(true);
                                    event.setDragDetect(true);

                                    lastParent = (VBox) taskPane.getParent();

                                    vBox.getChildren().remove(taskPane);
                                    taskPane.setLayoutX(event.getSceneX());
                                    taskPane.setLayoutY(event.getSceneY());
                                    mRootPane.getChildren().add(taskPane);

                                    selectedTask = (Task) taskPane.getUserData();

                                }
                            });

                            // jeśli klawisz myszy został puszczony
                            taskPane.setOnMouseReleased(event -> {
                                // Sprawdzenie, czy kliknięto w obrębie taskPane, a nie w obrębie jego "dziecka"
                                // ma to zapobiegać używania tej funkcji w momencie, gdy klikniemy w np. label "tytół taska"
                                // (wtedy zostanie użyta funkcja przypisana właśnie do elementu label, a nie ta)
                                if (event.getTarget() == taskPane) {
                                    taskPane.setMouseTransparent(false);

                                    mRootPane.getChildren().remove(taskPane);
                                    taskPane.setLayoutX(0);
                                    taskPane.setLayoutY(0);
                                    lastParent.getChildren().add(taskPane);

                                    ObservableList<Node> workingCollection = FXCollections.observableArrayList(lastParent.getChildren());
                                    int lastElementIndex = workingCollection.size() - 1;
                                    Collections.swap(workingCollection, lastElementIndex - 1, lastElementIndex);
                                    lastParent.getChildren().setAll(workingCollection);

                                    addTaskToNewStatus();
                                }
                            });

                            // jeśli element jest przeciągany
                            taskPane.setOnMouseDragged(event -> {
                                taskPane.setLayoutX(event.getSceneX());
                                taskPane.setLayoutY(event.getSceneY());

                                event.setDragDetect(false);
                            });

                            // jeśli zostało wykryte przeciąganie
                            taskPane.setOnDragDetected(event -> {
                                taskPane.startFullDrag();
                            });

                            vBox.getChildren().add(taskPane);
                        }

                        // Przycisk do wyświetlania panelu do dodawania nowego taska
                        Pane addNewTaskButton = getNewTaskPane();
                        addNewTaskButton.getStyleClass().add("hover-hand-cursor");
                        addNewTaskButton.setOnMouseClicked(event -> {
                            VBox source = (VBox) ( (Pane) event.getSource() ).getParent();

                            selectedStatus = (Status) vBox.getUserData();
                            closeActiveAddNewTaskPane();
                            addNewTaskPane(source);
                        });
                        vBox.getChildren().add(addNewTaskButton);

                        // Funkcje do obsługi Drag and Drop po stronie elementu celu
                        // jeśli mysz znalazła się w obrębie elementu
                        vBox.setOnMouseDragEntered(event -> {

                        });

                        // jeśli mysz jest w obrębie elementu i się porusza
                        vBox.setOnMouseDragOver(event -> {

                        });

                        // jeśli mysz upuściła coś w obrębie elementu
                        vBox.setOnMouseDragReleased(event -> {
                            nextParent = vBox;
                        });

                        // jeśli mysz opuściła obręb elementu
                        vBox.setOnMouseDragExited(event -> {

                        });

                        mStatusesList.getChildren().add(vBox);
                    }

                    // ostatni VBox z przuciskiem do dodawania nowego statusu
                    VBox vBox = getVBox();
                    Pane addStatus = getStatusTitlePane("nowy status", "/imgs/addtaskwhite.png");
                    addStatus.getStyleClass().add("hover-hand-cursor");
                    addStatus.setOnMouseClicked(this::addNewGroupTask);
                    vBox.getChildren().add(addStatus);
                    mStatusesList.getChildren().add(vBox);

                    mUsersInProjectList.getItems().clear();
                    mUsersInProjectList.getItems().addAll(activeProject.getUsers());

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
        pane.setUserData(task);
        pane.getStyleClass().add("task-view");
        pane.setPadding(new Insets(10, 10, 10, 10));

        // Tytuł taska
        Label taskTitle = new Label(task.getName());
        taskTitle.setLayoutX(14.0);
        taskTitle.setLayoutY(7.0);
        taskTitle.getStyleClass().add("task-title");
        taskTitle.getStyleClass().add("hover-hand-cursor");

        taskTitle.setOnMouseClicked(this::showComments);

        pane.getChildren().add(taskTitle);

        // Panel z priorytetem
        Pane priority = new Pane();
        priority.setLayoutX(281.0);
        priority.setLayoutY(9.0);
        priority.getStyleClass().add("priority");
        pane.getChildren().add(priority);

        // Panel ze skrótem nazwy przydzielonego do taska pracownika
        if (task.getGranted_to() != null) {
            User user = getUserFromListById(task.getGranted_to(), activeProject.getUsers());

            if (user != null) {
                Label userShortcut = getUserAvatar(user);
                userShortcut.setLayoutX(13.0);
                userShortcut.setLayoutY(35.0);
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
     * Metoda służaca do wyszukiwania użytkownika na liście w szczegółach aktywnego projektu
     * @param id id szukanego użytkownika
     * @return zwraca obiekt znalezionega użytkownika lub null w przypadku jego nie znalezienia
     */
    private User getUserFromListById(Integer id, List<User> list) {
        if (list != null) {
            if (id == null)
                return null;

            for (User user : activeProject.getUsers()) {
                if (user.getId() == id) {
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Metoda do rysowania awatara użytkownika ze skrótem jego nazwy lub plusem w środku
     * @param user użytkownik, którego awatar ma być narysowany
     * @return awatar ze skrótem nazwy użytkownika lub z plusem, jeśli użytkownik wynosi null
     */
    private Label getUserAvatar(User user) {
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add("user-circle");

        if (user != null) {
            label.setText(user.getShortcut());
            label.setUserData(user);
        } else {
            label.setText("+");
        }

        return label;
    }

    private Label getUserAvatar() {
        return getUserAvatar(null);
    }

    /**
     * Metode zwracajjąca panel zawierający przycisk do tworzenia nowego taska
     * @return panel "dodaj nowe zadanie"
     */
    private Pane getNewTaskPane() {
        // Panel taska
        Pane pane = new HBox();
        pane.getStyleClass().add("add-new-task");
        pane.setMaxWidth(180.0);
        pane.setPadding(new Insets(5, 5, 5, 5));
        ((HBox) pane).setSpacing(10);
        ((HBox) pane).setAlignment(Pos.CENTER);

        // Ikona
        ImageView editIcon = new ImageView(new Image("/imgs/addtask.png"));
        editIcon.setFitWidth(20.0);
        editIcon.setFitHeight(20.0);
        pane.getChildren().add(editIcon);

        // "Dodaj nowe zadanie"
        Label addTaskLabel = new Label("dodaj nowe zadanie");
        addTaskLabel.getStyleClass().add("new-task-add-label");
        pane.getChildren().add(addTaskLabel);

        return pane;
    }

    private Object getParentData(Event event) {
        return ( (Node)event.getSource() ).getParent().getUserData();
    }

    private Object getParentData(Node node) {
        return node.getParent().getUserData();
    }

    /**
     * Funkcja dodająca do podanego kontenera panel do dodawania nowego taska
     * @param parent kontener, do którego ma zostać dodany panel
     */
    public void addNewTaskPane(Pane parent) {
        Pane pane = new Pane();
        activeAddNewTaskPane = pane;
        pane.getStyleClass().add("new-task-description");
        pane.setMaxWidth(250.0);
        pane.setMaxHeight(150.0);
        pane.setPadding(new Insets(25, 25, 25, 25));

        // Nazwa nowego taska
        TextField newTaskName = new TextField();
        newTaskName.setPromptText("Tytół nowego zadania...");
        newTaskName.setLayoutX(14.0);
        newTaskName.setLayoutY(15.0);
        newTaskName.setPrefWidth(186.0);
        pane.getChildren().add(newTaskName);

        // Przycisk do zamykania okna
        ImageView closeBtn = new ImageView(new Image("/imgs/close.png"));
        closeBtn.setFitWidth(14.0);
        closeBtn.setFitHeight(14.0);
        closeBtn.setLayoutX(223.0);
        closeBtn.setLayoutY(14.0);
        closeBtn.getStyleClass().add("hover-hand-cursor");

        AtomicReference<Pane> addNewUserPane = null;

        // Funkcja usuwająca panel do dodawania nowego taska z VBox'a
        closeBtn.setOnMouseClicked(event ->  closeActiveAddNewTaskPane());
        pane.getChildren().add(closeBtn);

        // Przycisk "Przydziel osobę do zadania" (ikona awatara użytkownika)
        Label addUserToTaskBtn = getUserAvatar();
        addUserToTaskBtn.setLayoutX(25.0);
        addUserToTaskBtn.setLayoutY(55.0);
        addUserToTaskBtn.getStyleClass().add("hover-hand-cursor");

        addUserToTaskBtn.setOnMouseClicked(event -> {
            activeAddNewUserToTaskPane = getAddUserToNewTaskPane(addUserToTaskBtn);

            parent.getChildren().add(activeAddNewUserToTaskPane);
        });

        pane.getChildren().add(addUserToTaskBtn);

        Button addTaskBtn = new Button("Dodaj zadanie");
        addTaskBtn.setLayoutX(79.0);
        addTaskBtn.setLayoutY(100.0);
        addTaskBtn.getStyleClass().add("add-new-task-btn");
        addTaskBtn.setOnAction( event -> {
            User user = (User) addUserToTaskBtn.getUserData();
            addNewTask(newTaskName.getText(), user);
        });
        pane.getChildren().add(addTaskBtn);

        parent.getChildren().add(pane);
    }

    private Pane getAddUserToNewTaskPane(Label userLabel) {
        Pane pane = new Pane();
        pane.getStyleClass().add("users-inproject-list");
        pane.setMaxWidth(250.0);
        pane.setPadding(new Insets(10, 10, 10, 10));

        // Przycisk do zamykania okna
        ImageView closeBtn = new ImageView(new Image("/imgs/close.png"));
        closeBtn.setFitWidth(14.0);
        closeBtn.setFitHeight(14.0);
        closeBtn.setLayoutX(229.0);
        closeBtn.setLayoutY(7.0);
        closeBtn.getStyleClass().add("hover-hand-cursor");

        // Funkcja usuwająca panel do dodawania nowego taska z VBox'a
        closeBtn.setOnMouseClicked(event -> closeActiveAddNewUserToTaskPane());
        pane.getChildren().add(closeBtn);

        // Lista użytkowników
        ListView<User> listView = new ListView<User>();
        listView.setMaxWidth(200.0);
        listView.setMaxHeight(100.0);
        listView.setLayoutX(22.0);
        listView.setLayoutY(10.0);
        listView.getItems().addAll(activeProject.getUsers());
        pane.getChildren().add(listView);

        // Przycisk "Dodaj użytkownika"
        Button addUserBtn = new Button("Dodaj/Zmień użytkownika");
        addUserBtn.setLayoutX(47.0);
        addUserBtn.setLayoutY(120.0);
        addUserBtn.getStyleClass().add("add-new-task-btn");
        addUserBtn.setOnAction( event -> {
            User user = listView.getSelectionModel().getSelectedItem();

            if (user != null) {
                userLabel.setText(user.getShortcut());
                userLabel.setUserData(user);
                closeActiveAddNewUserToTaskPane();
            }
        });
        pane.getChildren().add(addUserBtn);

        // Przycisk "Dodaj użytkownika"
        Button removeUserBtn = new Button("Usuń użytkownika");
        removeUserBtn.setLayoutX(67.0);
        removeUserBtn.setLayoutY(150.0);
        removeUserBtn.getStyleClass().add("red-btn");
        removeUserBtn.setOnAction( event -> {
            userLabel.setText("+");
            userLabel.setUserData(null);
            closeActiveAddNewUserToTaskPane();
        });
        pane.getChildren().add(removeUserBtn);

        return pane;
    }

    /**
     * Funkcja zamykająca aktywny panel do dodawania nowego taska
     */
    public void closeActiveAddNewTaskPane() {
        closeActiveAddNewUserToTaskPane();
        if (activeAddNewTaskPane != null) {
            removeNodeFromPanel(activeAddNewTaskPane);
            activeAddNewTaskPane = null;
        }
    }

    /**
     * Funkcja zamykająca aktywny panel do dodawania nowego taska
     */
    public void closeActiveAddNewUserToTaskPane() {
        if (activeAddNewUserToTaskPane != null) {
            removeNodeFromPanel(activeAddNewUserToTaskPane);
            activeAddNewUserToTaskPane = null;
        }
    }

    /**
     * Funkcja usuwająca węzęł z kontenera nadrzędnego
     * @param node Węzęł do usunięcia
     */
    public void removeNodeFromPanel(Node node) {
        ((Pane) node.getParent()).getChildren().remove(node);
    }

    /**
     * Metoda służąca do odświeżania panelu ze szczegółowymi informacjami o tasku
     */
    public void refreshTaskDetails() {
        if ( selectedTask != null ) {
            int task_id = selectedTask.getTask_id();

            RequestService requestService = new RequestService();

            RsTaskDetails response;
            try {
                response = requestService.getTaskDetails(task_id);

                if (response.isSuccess()) {
                    activeTask = response;

                    mTaskTitleInCommentsPanel.setText(activeTask.getTask_name());
                    mTaskDescription.setText(activeTask.getTask_desc());

                    User user = getUserFromListById(activeTask.getAssigned_to(), activeTask.getUsers());
                    if (user != null) {
                        mUserInTaskDetails.setText(user.getShortcut());
                        mUserInTaskDetails.setUserData(user);
                    } else {
                        mUserInTaskDetails.setText("+");
                    }

                    mUserInTaskDetails.setOnMouseClicked(event -> {
                        activeAddNewUserToTaskPane = getAddUserToNewTaskPane(mUserInTaskDetails);
                        activeAddNewUserToTaskPane.setLayoutX(35.0);
                        activeAddNewUserToTaskPane.setLayoutY(96.0);
                        mCommentsPanel.getChildren().add(activeAddNewUserToTaskPane);
                    });

                    mCommentsPanel.setVisible(true);
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można wyświetlić szczegółów zadania. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        }
    }

    public void addNewTask(String task_name, User user) {
        if (!task_name.isEmpty()){
            String domain = getDomain();
            int project_id = activeProject.getProject_id();
            int status_id = selectedStatus.getStatus_id();
            int creator_id = getUserId();
            Integer assigned_to = null;

            if (user != null)
                assigned_to = user.getId();

            RequestService requestService = new RequestService();
            RqTask requestObject = new RqTask(domain, project_id, status_id, creator_id, task_name, assigned_to);
            RsNewTask response;
            try {
                response = requestService.createNewTask(requestObject);

                if (response.getMsg().equals("Task exists in this project"))
                    showErrorPanel("Błąd: Projekt o takiej nazwie już istnieje.");
                else if (response.isSuccess()) {
                    showInfoPanel("Sukces: Utworzono nowe zadanie o nazwie: " + response.getTask_title());
                    refreshProjectDetails(getSelectedProject());
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można stworzyć nowego projektu. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        } else {
            showErrorPanel("Błąd: Proszę podać nazwę nowego zadania.");
        }
    }

    private void addTaskToNewStatus() {
        Integer status_id = ((Status) nextParent.getUserData()).getStatus_id();

        RqTask requestObject = new RqTask();
        requestObject.setStatus_id(status_id);

        editTask(requestObject);
    }

    /**
     * Metoda do edycji taska.
     * @param requestObject RqTask, który zostanie przekonwertowany na ciało zapytania.
     */
    public boolean editTask(RqTask requestObject) {
        requestObject.setDomain(getDomain());
        requestObject.setProject_id(activeProject.getProject_id());
        requestObject.setTask_id(selectedTask.getTask_id());
        requestObject.setCreator_id(getUserId());

        RequestService requestService = new RequestService();
        BaseResponseObject response;
        try {
            response = requestService.editTask(requestObject);
            /*
            if (response.getMsg().equals("))
                showErrorPanel("Błąd:");
            else */if (response.isSuccess()) {
                refreshProjectDetails(getSelectedProject());
                return true;
            } else if (!response.isSuccess())
                DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
        } catch (IOException e) {
            DialogsUtils.shortErrorDialog("Błąd", "Nie można przenieść zadania do innego statusu statusu. Błąd połączenia z serwerem.");
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    void backToHome(MouseEvent event) {
        hideProjectDetails();
    }

    @FXML
    void checkNewProjectInputValue(InputMethodEvent event) {}

    @FXML
    void createNewProject(MouseEvent event) {}

    @FXML
    void showWorkspaces(MouseEvent event) {}

    @FXML
    public void checkNewProjectInputValue(javafx.scene.input.InputMethodEvent inputMethodEvent) {}

    @FXML // TODO funckja do dodawania nowego komentarza
    void addNewComment(MouseEvent event) {}

    @FXML
    void editTaskActionEvent(ActionEvent event) {
        String task_name = mTaskTitleInCommentsPanel.getText();
        String task_desc = mTaskDescription.getText();
        Integer assigned_to = null;

        // Pobieranie id z elementu, który przechowywuje dane o obiekcie user
        User user = (User) mUserInTaskDetails.getUserData();
        if (user != null) {
            assigned_to = user.getId();
        }

        RqTask requestObject = new RqTask();

        // Jeśli wartości inputów różnią się od tych w tasku, to należy je wstawić do ciała zapytania
        // aby je zmodyfikować
        if (!task_name.equals(activeTask.getTask_name()))
            requestObject.setTask_name(task_name);

        if (!task_desc.equals(activeTask.getTask_desc()))
            requestObject.setTask_desc(task_desc);

        if (assigned_to == null) {
            // Jeśli było ustawione id użytkownika (czyli jest różne od null), ustawiamy je na 0 aby go usunąć z taska
            if (activeTask.getAssigned_to() != null) {
                requestObject.setAssigned_to(0);
            }
        } else {
            if (!assigned_to.equals(activeTask.getAssigned_to())) {
                requestObject.setAssigned_to(assigned_to);
            }
        }

        boolean isSuccess = editTask(requestObject);

        if (isSuccess)
            showInfoPanel("Zmodyfikowano informacje o zadaniu.");
    }

    @FXML
    void addNewTaskActionEvent(ActionEvent event) {}

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
        closeTaskDescriptionPanel();
    }

    private void closeTaskDescriptionPanel() {
        mCommentsPanel.setVisible(false);
        activeTask = null;
    }

    @FXML
    void sendInvitation(MouseEvent event) {}

    @FXML
    void showComments(MouseEvent event) {
        selectedTask = (Task) getParentData(event);
        closeAllPanels();
        refreshTaskDetails();
    }

    @FXML
    void showInvitationPanel(MouseEvent event) {
        closeAllPanels();
        mInvitationPanel.setVisible(true);

        refreshProjectDetails(getSelectedProject());
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
        User user = mUsersInDomainList.getSelectionModel().getSelectedItem();

        if (user != null) {
            int user_id = getUserId();
            int project_id = activeProject.getProject_id();
            String domain = getDomain();
            int assigned_to = user.getId();

            RequestService requestService = new RequestService();
            RqUser requestObject = new RqUser(user_id, project_id, domain, assigned_to);

            BaseResponseObject response;
            try {
                response = requestService.addUserToProject(requestObject);

                if (response.getMsg().equals("User already added to project"))
                    showInfoPanel("Użytkownik jest już dodany do projektu.");
                else if (response.isSuccess()) {
                    showInfoPanel("Użytkownik o nazwie " + user.getName() + " został dodany do projektu.");
                    refreshProjectDetails(getSelectedProject());
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można dodać użytkownika do projektu. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        } else {
            showErrorPanel("Proszę wybrać użytkownika, który ma zostać dodany do projektu.");
        }
    }

    @FXML  // Funkcja do usuwania użytkownika z projektu
    public void showDeleteUserPanelHandler(MouseEvent mouseEvent) {
        if (getSelectedUserInProjectList() != null)
            showDeleteUsersPane();
        else
            showErrorPanel("Proszę wybrać użytkownika do usunięcia.");
    }

    @FXML
    public void deleteUserFromProject(ActionEvent actionEvent) {
        User user = getSelectedUserInProjectList();

        if (user != null) {
            int user_id = getUserId();
            int project_id = activeProject.getProject_id();
            int assigned_to = user.getId();

            RequestService requestService = new RequestService();
            RqUser requestObject = new RqUser(user_id, project_id, assigned_to);

            BaseResponseObject response;
            try {
                response = requestService.removeUserFromProject(requestObject);

                if (response.isSuccess()) {
                    showInfoPanel("Użytkownik o nazwie " + user.getName() + " został usunięty z projektu.");
                    closeDeleteUserPane();
                    refreshProjectDetails(getSelectedProject());
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można usunąć użytkownika do projektu. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        } else {
            showErrorPanel("Proszę wybrać użytkownika do usunięcia.");
        }
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
        if (!project_name.isEmpty()) {
            int user_id = getUserId();
            int domain_id = mChooseWorkspace.getValue().getId();

            RequestService requestService = new RequestService();
            RqNewProject newProject = new RqNewProject(user_id, project_name, domain_id);
            RsProject response;
            try {
                response = requestService.createNewProject(newProject);

                if (response.getMsg().equals("Project name already exist inside this domain"))
                    showErrorPanel("Błąd: Projekt o takiej nazwie już istnieje.");
                else if (response.isSuccess()) {
                    showInfoPanel("Sukces: Utworzono nowy projekt o nazwie: " + response.getProject_name());
                    refresh();
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można stworzyć nowego projektu. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        } else {
            showErrorPanel("Błąd: Proszę podać nazwę projetu.");
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
    public void closePdfGeneratorPanel(MouseEvent event) {
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
    public void closeDeleteUserPaneHandler(MouseEvent mouseEvent) {
        closeDeleteUserPane();
    }

    @FXML
    public void cancelDeleteUserPaneHandler(ActionEvent actionEvent) {
        closeDeleteUserPane();
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

        //TODO WEBVIEW RAPORTY
        public void showPdfGeneratorPanel(MouseEvent mouseEvent) {
            WebView webView = new WebView();

            webView.getEngine().load("http://ssh-vps.nazwa.pl:4742/reports/render?user_id=" + getUserId() + "&type=1&domain=1&params=all");

            AnchorPane pane = new AnchorPane(webView);
            AnchorPane.setTopAnchor(webView, 0.0);
            AnchorPane.setLeftAnchor(webView, 0.0);
            AnchorPane.setRightAnchor(webView, 0.0);
            AnchorPane.setBottomAnchor(webView, 0.0);

            Scene raportsScene = new Scene(pane);

            Stage raportsStage = new Stage();
            raportsStage.setWidth(1024.0);
            raportsStage.setHeight(800.0);
            raportsStage.setScene(raportsScene);
            raportsStage.setTitle("Devslog raports");
            raportsStage.show();

            try {
                PdfGenerator.generate("http://ssh-vps.nazwa.pl:4742/reports/render?user_id=15&type=1&domain=1&params=all", "raport.pdf");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}
