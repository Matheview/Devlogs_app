package controllers;

import backend.RequestData;
import backend.responseObjects.User;
import backend.responseObjects.Domain;
import backend.RequestService;
import backend.ResponseObject;
import backend.responseObjects.RsDomains;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.DialogsUtils;
import utils.RegexUtils;

import java.awt.event.InputMethodEvent;
import java.io.IOException;

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
    private ToggleGroup mAccountType;

    @FXML
    private RadioButton mAdminType;

    @FXML
    private RadioButton mBossType;

    @FXML
    private RadioButton mUserType;

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

        refresh();
    }

    /**
     * Metoda odświeżająca listy domen i użytkowników
     */
    @Override
    public void refresh() {
        RequestService requestService = new RequestService();

        RsDomains domains;
        try {
            domains = requestService.getUserDomains(Controller.currAcc.getUser_id());
            mWorkspaceList.getItems().addAll(domains.getDomains());
        } catch (IOException e) {
            DialogsUtils.shortErrorDialog("Błąd", "Nie można pobrać listy domen z serwera. Błąd połączenia z serwerem.");
            e.printStackTrace();
        }

        try {
            ResponseObject responseObject = requestService.requestListOfUsers(Controller.currAcc.getUser_id());
            mUserlist.getItems().addAll(responseObject.getUsers());
        } catch (IOException e) {
            DialogsUtils.shortErrorDialog("Błąd", "Nie można pobrać listy domen z serwera. Błąd połączenia z serwerem.");
            e.printStackTrace();
        }
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
        RadioButton selectedButton = (RadioButton) mAccountType.getSelectedToggle();

        String email = mEmail.getText();
        String password = mPassword.getText();
        String domain = Controller.currAcc.getDomain();
        String privilage = selectedButton.getText();
        String username = mUserName.getText();
        int user_id = Controller.currAcc.getUser_id();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty())
            DialogsUtils.shortErrorDialog("Błąd", "Proszę wypełnić wszystkie pola.");
        else if (!RegexUtils.validateEmail(email))
            DialogsUtils.shortErrorDialog("Błąd", "Wpisano niepoprawny adres e-mail..");
        else {
            RequestService requestService = new RequestService();
            RequestData requestData = new RequestData(email, password, domain, user_id, privilage, username);

            Gson gson = new Gson();
            String inputJSON = gson.toJson(requestData);
            ResponseObject response;
            try {
                response = requestService.requestCreateNewUser(inputJSON);

                if (response.getMsg().equals("Email alredy exists"))
                    DialogsUtils.shortErrorDialog("Błąd", "Użytkownik o takim emailu już istnieje..");
                else if (response.isSuccess()) {
                    DialogsUtils.infoDialog("Sukces", "Utworzono nowego użytkownika", "Utworzono nowego użytkownika: " + response.getUsername());
                    refresh();
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można stworzyć nowego użytkownika. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        }
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
