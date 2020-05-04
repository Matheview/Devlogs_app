package controllers;

import backend.User;
import backend.responseObjects.Domain;
import backend.RequestService;
import backend.ResponseObject;
import backend.responseObjects.RsDomains;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.event.InputMethodEvent;

public class AdminController extends BaseController {

    //ZMIENNE - NIE WSZYSTKIE BĘDĄ POTRZEBNE

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
    private ListView<Domain> mWorkspaceList;

    @FXML
    private TextField mUserSearch;

    @FXML
    private ListView<User> mUserlist;

    @FXML
    private ImageView mCloseNotificationPanelIcon;

    @FXML
    private Pane mNotificationsPanel;

    @FXML
    private ListView<?> mNotificationsList;

    @FXML
    private ImageView mLogoutIcon;

    @FXML
    private Label mWelcomeUserName;

    @FXML
    private Label mPrivilegeUser;

    @FXML
    private Label mNotificationsCounter;

    //Views initialize
    public void initialize() {
        mWelcomeUserName.setText(Controller.currAcc.getUsername());
        mPrivilegeUser.setText(Controller.currAcc.getPrivilege());

        RequestService requestService = new RequestService();

        RsDomains domains = requestService.getUserDomains(Controller.currAcc.getUser_id());
        mWorkspaceList.getItems().addAll(domains.getDomains());

        ResponseObject responseObject = requestService.requestListOfUsers(Controller.currAcc.getUser_id());
        mUserlist.getItems().addAll(responseObject.getUsers());
    }

    //Metody ( nie wszystkie metody i zmienne będą potrzebne, ale są wyciągnięte w razie W )----------------------------------------------------

    @FXML //Metoda obsługująca powrót do strony głównej
    void backToHome(MouseEvent event) {

    }

    @FXML //Metoda zamykająca panel powiadomień
    void closeNotificationsPanel(MouseEvent event) {
    mNotificationsPanel.setVisible(false);
    }

    @FXML //Metoda sprawdzająca czy checkbox z typem konta admin jest true
    void handleAdminCheck(ActionEvent event) {

    }

    @FXML //Metoda sprawdzająca czy checkbox z typem konta kierownik jest true
    void handleBossCheck(ActionEvent event) {

    }

    @FXML //Metoda sprawdzająca zawartość inputa do emaila
    void handleEmailChange(InputMethodEvent event) {

    }

    @FXML //Metoda filtrująca userów i listująca ich na liście
    void handleFindUser(InputMethodEvent event) {

    }

    @FXML //Metoda filtrująca przestrzenie i listująca je na liście
    void handleFindWorkspace(InputMethodEvent event) {

    }

    @FXML //Metoda sprawdzająca zawartość inputa hasła
    void handlePasswordChange(InputMethodEvent event) {

    }

    @FXML //Metoda sprawdzająca czy checkbox z typem konta użytkownik jest true
    void handleUserCheck(ActionEvent event) {

    }

    @FXML //Metoda sprawdzająca zawartość inputa z nazwą użytkownika
    void handleUserNameChange(InputMethodEvent event) {

    }

    @FXML //Metoda sprawdzająca zawartość inputa z nazwą przestrzeni
    void handleWorkspaceNameChange(InputMethodEvent event) {

    }

    @FXML //Metoda wylogowywująca usera
    void logoutUser(MouseEvent event) {
        Controller.currAcc = null;
        getController().clearFields();
        getController().showWindow();
        // get a handle to the stage
        Stage stage = (Stage) mLogoutIcon.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    @FXML //Metoda do przycisku wysyłająca dane z inputów na serwer w celu stworzenia nowego usera
    void makeNewUser(ActionEvent event) {

    }

    @FXML //Metoda do przycisku wysyłająca dane z inputa na serwer w celu stworzenia nowej przestrzeni
    void makeNewWorkspace(ActionEvent event) {

    }

    @FXML //Metoda wywołująca panel powiadomień
    void showNotificationPanel(MouseEvent event) {
    mNotificationsPanel.setVisible(true);
    }

    @FXML
    public void showNotificationsPanel(MouseEvent mouseEvent) {
    mNotificationsPanel.setVisible(true);
    }

    @FXML
    public void checkNewProjectInputValue(javafx.scene.input.InputMethodEvent inputMethodEvent) {
    }

    @FXML
    public void showWorkspaces(MouseEvent mouseEvent) {
    }

    @FXML
    public void createNewProject(MouseEvent mouseEvent) {
    }

    @FXML
    public void handleUserNameChange(javafx.scene.input.InputMethodEvent inputMethodEvent) {
    }

    @FXML
    public void handleEmailChange(javafx.scene.input.InputMethodEvent inputMethodEvent) {
    }

    @FXML
    public void handlePasswordChange(javafx.scene.input.InputMethodEvent inputMethodEvent) {
    }

    @FXML
    public void handleWorkspaceNameChange(javafx.scene.input.InputMethodEvent inputMethodEvent) {
    }

    @FXML
    public void handleFindWorkspace(javafx.scene.input.InputMethodEvent inputMethodEvent) {
    }

    @FXML
    public void handleFindUser(javafx.scene.input.InputMethodEvent inputMethodEvent) {
    }
    //FUNKCJE DO OBSŁUŻENIA BACKEND
}
