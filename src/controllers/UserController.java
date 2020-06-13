package controllers;

import backend.RequestService;
import backend.dataObjects.*;
import backend.requestObjects.RqNewComment;
import backend.requestObjects.RqTask;
import backend.responseObjects.*;
import components.CommentPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.DialogsUtils;

import java.io.IOException;
import java.util.Collections;

public class UserController extends BaseController {

    /**
     * Projekt, którego szczegóły są obecnie wyświetlane
     */
    protected RsProjectDetails activeProject;

    /**
     * Status, który został wybrany do np. edycji lub usunięcia
     */
    protected Status selectedStatus;

    /**
     * Task, który został wybrany do np. edycji lub usunięcia
     */
    protected Task selectedTask;

    /**
     * Task, którego szczegóły są obecnie wyświetlane (zawiera więcej informacji niż selectedTask)
     * Pobierany jest w momencie użycia metody refreshTaskDetails()
     */
    protected RsTaskDetails activeTask;

    /**
     * Aktywny panel do dodawania nowego taska
     */
    protected Pane activeAddNewTaskPane;

    /**
     * Aktywny panel do dodawania nowego użytkownika do taska
     */
    protected Pane activeAddNewUserToTaskPane;

    /**
     * Zmienna przechowująca referencję do ostatniego rodzica panelu taska
     */
    protected VBox lastParent;

    /**
     * Zmienna przechowująca referencję do następnego rodzica panelu taska
     */
    protected VBox nextParent;

    @FXML
    protected ListView<Project> mProjectsList;

    @FXML
    protected ImageView mCloseNotificationPanelIcon;

    @FXML
    protected ScrollPane mInProjectContainer;

    @FXML
    protected Label mTaskTitle;

    @FXML
    protected Pane mTask;

    @FXML
    protected Label mTaskStartDate;

    @FXML
    protected Label mTaskDeadline;

    @FXML
    protected Pane mTaskPriority;

    @FXML
    protected Label mTaskCommentsCount;

    @FXML
    protected Pane mCommentsPanel;

    @FXML
    protected TextField mTaskTitleInCommentsPanel;

    @FXML
    protected TextArea mTaskDescription;

    @FXML
    protected VBox mTaskComments;

    @FXML
    protected TextField mMyComment;

    @FXML
    protected Pane mInvitationPanel;

    @FXML
    protected Label mProjectTitle;

    @FXML
    protected Pane mProjectNavbar;

    @FXML
    protected Pane mRootPane;

    @FXML
    protected HBox mStatusesList;

    @FXML
    protected Label mUserInTaskDetails;

    @FXML
    protected ListView<User> mUsersInProjectList;

    // ------- panel raportów -------

    @FXML
    protected Pane mPdfGeneratorPanel;


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
     * Metoda zwracająca aktualnie wybrany projekt na liście
     */
    public Project getSelectedProject() {
        return mProjectsList.getSelectionModel().getSelectedItem();
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
                        Pane statusName = getStatusTitlePane(status.getName());
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

                        // Lista użytkowników w projekcie
                        mUsersInProjectList.getItems().clear();
                        mUsersInProjectList.getItems().addAll(activeProject.getUsers());

                    }
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można pobrać szczegułów projekcie. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        }
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

    /**
     * Metoda zwracająca standardowy dla tej aplikacji VBox
     * @return standardoty VBox
     */
    protected VBox getVBox() {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.TOP_CENTER);

        return vBox;
    }

    /**
     * Metoda zwracajjąca panel zawierający nazwę statusu (bez przycisków do edycji i usuwania)
     * @param title nazwa statusu
     * @return panel z tytułem statusu
     */
    protected Pane getStatusTitlePane(String title) {
        // Panel statusu
        Pane pane = new Pane();
        pane.getStyleClass().add("tasks-list");

        pane.setPadding(new Insets(5, 5, 5, 5));

        // Tytuł statusu
        Label titleLabel = new Label(title);
        titleLabel.setLayoutX(47.0);
        titleLabel.setLayoutY(5.0);
        titleLabel.getStyleClass().add("topic-name");
        pane.getChildren().add(titleLabel);

        return pane;
    }

    /**
     * Metode zwracajjąca panel zawierający dane o tasku
     * @param task obiekt taska
     * @return panel z informacjami o tasku
     */
    protected Pane getTaskPane(Task task) {
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
            User user = User.getUserFromListById(task.getGranted_to(), activeProject.getUsers());

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
     * Metoda dodajęca task do nowego statusu
     */
    protected void addTaskToNewStatus() {
        Integer status_id = ((Status) nextParent.getUserData()).getStatus_id();

        RqTask requestObject = new RqTask();
        requestObject.setStatus_id(status_id);

        editTask(requestObject);
    }

    protected Object getParentData(Event event) {
        return ( (Node)event.getSource() ).getParent().getUserData();
    }

    protected Object getParentData(Node node) {
        return node.getParent().getUserData();
    }

    /**
     * Metoda do rysowania awatara użytkownika ze skrótem jego nazwy lub plusem w środku
     * @param user użytkownik, którego awatar ma być narysowany
     * @return awatar ze skrótem nazwy użytkownika lub z plusem, jeśli użytkownik wynosi null
     */
    protected Label getUserAvatar(User user) {
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

    protected Label getUserAvatar() {
        return getUserAvatar(null);
    }

    /**
     * Ukryj panel ze szczegółami projektu
     */
    protected void hideProjectDetails() {
        closeAllPanels();
        mInProjectContainer.setVisible(false);
        mProjectNavbar.setVisible(false);
        mNavbar.setVisible(true);
        mMain.setVisible(true);
    }

    /**
     * Funkcja zamykająca wszystkie pomniejsze panele
     */
    public void closeAllPanels() {
        mInvitationPanel.setVisible(false);
        closeTaskDescriptionPanel();
    }

    protected void closeTaskDescriptionPanel() {
        mCommentsPanel.setVisible(false);
        activeTask = null;
    }

    /**
     * Metoda do edycji taska.
     * @param requestObject RqTask, który zostanie przekonwertowany na ciało zapytania.
     */
    protected boolean editTask(RqTask requestObject) {
        requestObject.setTask_id(selectedTask.getTask_id());
        requestObject.setUser_id(getUserId());

        RequestService requestService = new RequestService();
        BaseResponseObject response;
        try {
            response = requestService.updateTask(requestObject);

            if (response.getMsg().equals("Comment not allow to user")) {
                showErrorPanel("Błąd: nie można zmieniać statusu zadań, do których nie jest się przydzielonym.");
                refreshProjectDetails(getSelectedProject());
            } else if (response.isSuccess()) {
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
    protected void editTaskActionEvent(ActionEvent event) {
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
    protected void addNewComment(ActionEvent event) {
        String comment_desc = mMyComment.getText();

        if (!comment_desc.isEmpty()) {
            int user_id = getUserId();
            int task_id = activeTask.getTask_id();

            RequestService requestService = new RequestService();
            RqNewComment requestObject = new RqNewComment(user_id, task_id, comment_desc);

            BaseResponseObject response;
            try {
                response = requestService.addCommentToTask(requestObject);

                if (response.isSuccess()) {
                    refreshTaskDetails();
                    mMyComment.clear();
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można dodać komentarza do zadania. Błąd połączenia z serwerem.");
                e.printStackTrace();
            } catch (Exception e) {
                DialogsUtils.errorDialog("Błąd", "Coś poszło nie tak...", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void backToHome(MouseEvent event) {
        hideProjectDetails();
    }

    @FXML
    protected void showComments(MouseEvent event) {
        selectedTask = (Task) getParentData(event);
        closeAllPanels();
        refreshTaskDetails();

        // Wyświetlenie panelu ze szczegółami taska
        mCommentsPanel.setVisible(true);
    }

    @FXML
    protected void showInvitationPanel(MouseEvent event) {
        closeAllPanels();
        mInvitationPanel.setVisible(true);

        refreshProjectDetails(getSelectedProject());
    }

    @FXML
    protected void hideCommentsPanel(MouseEvent event) {
        closeTaskDescriptionPanel();
    }

    @FXML
    protected void handleTitleTaskChange(InputMethodEvent event) {}

    @FXML
    protected void closeInvitationPanel(MouseEvent event) {
        mInvitationPanel.setVisible(false);
    }

    @FXML
    protected void closePdfGeneratorPanel(MouseEvent event) {
        mPdfGeneratorPanel.setVisible(false);
    }

    @FXML
    protected void handleCommentChange(InputMethodEvent inputMethodEvent) {}

    @FXML
    protected void acceptInfoPanel(MouseEvent event) {
        mInfoPanel.setVisible(false);
    }

    @FXML
    public void closeInfoPanel(MouseEvent event) {
        mInfoPanel.setVisible(false);
    }

}
