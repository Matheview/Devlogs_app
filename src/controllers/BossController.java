package controllers;

import backend.requestObjects.*;
import backend.responseObjects.*;
import backend.dataObjects.*;
import backend.RequestService;
import components.CommentPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import javafx.scene.web.WebView;
import utils.PdfGenerator;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Klasa bazowa dla wszystkich kontrolerów. Zawiera wszystkie elementy wspólne
 */
public class BossController extends UserController {

    @FXML
    protected ComboBox<Domain> mChooseWorkspace;

    @FXML
    protected TextField mNewProjectInput;

    @FXML
    protected Button mAddNewProject;

    @FXML
    protected Pane mEditStatusPane;

    @FXML
    protected TextField mEditStatusNameTextField;

    @FXML
    protected Pane mDeleteStatusPane;

    @FXML
    protected Label mDeleteStatusName;

    @FXML
    protected Pane mNewStatusPane;

    @FXML
    protected TextField mNewStatusName;

    @FXML
    protected Pane mAvailableUsersPanel; // panel z dostępnymi userami w domenie do dodania do projektu

    @FXML
    protected Pane mDeleteUserPane;

    @FXML
    protected Label mDeleteUserName;

    @FXML
    protected Pane mAddNewTask;

    @FXML
    protected ListView<User> mUsersInDomainList;

    @FXML
    protected Pane mNewTaskPanel;

    @FXML
    protected CheckBox mLowPriority;

    @FXML
    protected CheckBox mMediumPriority;

    @FXML
    protected CheckBox mHighPriority;

    @FXML
    protected Pane mUsersInProjectPanel;

    @FXML
    protected ListView<?> mUsersInTaskList;

    @FXML
    protected TextField mInputTaskTitle;


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
        super.refresh();

        RequestService requestService = new RequestService();

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
     * Metoda zwracająca aktualnie wybranego użytkownika na liście użytkowników w projekcie
     */
    public User getSelectedUserInProjectList() {
        return mUsersInProjectList.getSelectionModel().getSelectedItem();
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
    @Override
    public void closeAllPanels() {
        closeNewStatusPane();
        closeEditStatusPane();
        closeDeleteStatusPane();
        mAvailableUsersPanel.setVisible(false);
        super.closeAllPanels();
    }

    /**
     * Funkcja odświeżająca panel ze szczegółami projektu
     * @param project projekt do załadowania w panelu
     */
    @Override
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

                                    // Zamiana tasku z przedostatnim elementem w HBox'ie
                                    // (ponieważ przedostatni element to przycisk do dodawania nowego taska)
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

                    // Lista użytkowników w projekcie
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
     * Metoda zwracajjąca panel zawierający nazwę statusu (wraz z przyciskami do edycji i usuwania statusu)
     * @param title nazwa statusu
     * @return panel z tytułem statusu
     */
    private Pane getStatusTitlePane(String title, String icon) {
        Pane pane = getStatusTitlePane(title);

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

        return pane;
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
    @Override
    public void refreshTaskDetails() {
        if ( selectedTask != null ) {
            int task_id = selectedTask.getTask_id();

            RequestService requestService = new RequestService();

            RsTaskDetails response;
            try {
                response = requestService.getTaskDetails(task_id);

                if (response.isSuccess()) {
                    activeTask = response;

                    // Tytuł taska
                    mTaskTitleInCommentsPanel.setText(activeTask.getTask_name());

                    // Opis taska
                    mTaskDescription.setText(activeTask.getTask_desc());

                    // Wyświetlenie awataru użytkownika
                    User user = User.getUserFromListById(activeTask.getAssigned_to(), activeTask.getUsers());
                    if (user != null) {
                        mUserInTaskDetails.setText(user.getShortcut());
                        mUserInTaskDetails.setUserData(user);
                    } else {
                        mUserInTaskDetails.setText("+");
                    }

                    // Panel do dodawania użytkownika do taska
                    mUserInTaskDetails.setOnMouseClicked(event -> {
                        activeAddNewUserToTaskPane = getAddUserToNewTaskPane(mUserInTaskDetails);
                        activeAddNewUserToTaskPane.setLayoutX(35.0);
                        activeAddNewUserToTaskPane.setLayoutY(96.0);
                        mCommentsPanel.getChildren().add(activeAddNewUserToTaskPane);
                    });

                    // Wyświetlanie komentarzy
                    mTaskComments.getChildren().clear();

                    for (Comment comment : activeTask.getComments()) {
                        CommentPane commentPane = new CommentPane(comment, activeTask.getUsers());
                        mTaskComments.getChildren().add(commentPane);
                    }
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

    /**
     * Metoda do edycji taska.
     * @param requestObject RqTask, który zostanie przekonwertowany na ciało zapytania.
     */
    @Override
    protected boolean editTask(RqTask requestObject) {
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
                refreshTaskDetails();
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
    void checkNewProjectInputValue(InputMethodEvent event) {}

    @FXML
    void createNewProject(MouseEvent event) {}

    @FXML
    void showWorkspaces(MouseEvent event) {}

    @FXML
    public void checkNewProjectInputValue(javafx.scene.input.InputMethodEvent inputMethodEvent) {}

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

    @FXML
    void generatePdf(MouseEvent event) {}

    @FXML
    void handleInvitationInputChange(InputMethodEvent event) {}

    @FXML
    void sendInvitation(MouseEvent event) {}

    @Override
    @FXML
    protected void closeInvitationPanel(MouseEvent event) {
        mAvailableUsersPanel.setVisible(false);
        super.closeInvitationPanel(event);
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
        } catch (Exception e) {
            DialogsUtils.errorDialog("Błąd", "Coś poszło nie tak...", e.getMessage());
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
            } catch (Exception e) {
                DialogsUtils.errorDialog("Błąd", "Coś poszło nie tak...", e.getMessage());
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
            } catch (Exception e) {
                DialogsUtils.errorDialog("Błąd", "Coś poszło nie tak...", e.getMessage());
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
    protected void showUsersInProject(MouseEvent event) {
        mUsersInProjectPanel.setVisible(true);
    }

    @FXML
    public void handleInvitationInputChange(javafx.scene.input.InputMethodEvent inputMethodEvent) {}

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
            } catch (Exception e) {
                DialogsUtils.errorDialog("Błąd", "Coś poszło nie tak...", e.getMessage());
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
    public void addNewGroupTask(MouseEvent mouseEvent) {
        showNewStatusPane();
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
            } catch (Exception e) {
                DialogsUtils.errorDialog("Błąd", "Coś poszło nie tak...", e.getMessage());
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
            } catch (Exception e) {
                DialogsUtils.errorDialog("Błąd", "Coś poszło nie tak...", e.getMessage());
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
            } catch (Exception e) {
                DialogsUtils.errorDialog("Błąd", "Coś poszło nie tak...", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda do wyświetlania raportów
     * @param mouseEvent
     */
    public void showPdfGeneratorPanel(MouseEvent mouseEvent) {
        WebView webView = new WebView();

        webView.getEngine().load("http://ssh-vps.nazwa.pl:4742/reports/render?user_id=" + getUserId() + "&type=1&domain=1&params=all");

        // Panel główny
        AnchorPane pane = new AnchorPane(webView);
        pane.getStylesheets().add("styles/boss.css");

        AnchorPane.setTopAnchor(webView, 0.0);
        AnchorPane.setLeftAnchor(webView, 0.0);
        AnchorPane.setRightAnchor(webView, 0.0);
        AnchorPane.setBottomAnchor(webView, 0.0);

        // Przycisk "generuj raport"
        Button generatePdfBtn = new Button();
        generatePdfBtn.setText("Generuj raport");
        generatePdfBtn.getStyleClass().add("generate-pdf-btn");
        generatePdfBtn.setLayoutX(845);
        generatePdfBtn.setLayoutY(11);

        // Funkcja służąca do generowania raportów w formacie .pdf
        generatePdfBtn.setOnAction(event -> {
            try {
                // Dodawanie daty do nazwy raporu
                LocalDateTime localDate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
                String formattedString = localDate.format(formatter);

                String fileName = "report_" + formattedString + ".pdf";
                PdfGenerator.generate(webView.getEngine().getLocation(), fileName);
                DialogsUtils.infoDialog("Generowanie raportu", "Wygenerowano raport o nazwie:", fileName);
            } catch (IOException e) {
                DialogsUtils.errorDialog("Błąd", "Nie można wygenerować raportu: ", e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                DialogsUtils.errorDialog("Błąd", "Coś poszło nie tak...", e.getMessage());
                e.printStackTrace();
            }
        });
        pane.getChildren().add(generatePdfBtn);

        Scene raportsScene = new Scene(pane);

        Stage raportsStage = new Stage();
        raportsStage.setWidth(1024.0);
        raportsStage.setHeight(800.0);
        raportsStage.setScene(raportsScene);
        raportsStage.setTitle("Devslog reports");
        raportsStage.show();
    }

}
