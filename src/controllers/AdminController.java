package controllers;

import backend.RequestData;
import backend.responseObjects.RsUserInfo;
import backend.responseObjects.User;
import backend.responseObjects.Domain;
import backend.RequestService;
import backend.ResponseObject;
import backend.responseObjects.RsDomains;
import com.google.gson.Gson;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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
    private ImageView mSearchUserIcon;

    @FXML
    private ImageView mSearchWorkspaceIcon;

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

    // Zmienne do popapów informacyjnych ->

    @FXML
    private Pane mInfoPanel;

    @FXML
    private ImageView mInfoIcon;
    // Tutaj trzeba będzie zmieniać src zdjęcia w zależności czy to informacja czy warning

    @FXML
    private Label mTextInfoPanel;
    // Do tej zmiennej trzeba przypisywać tekst informacji w zależności od tego co użytkownik zrobił źle np. podał słabe hasło lub nie podał w ogóle itd.

    @FXML
    private Button mCloseInfoButton;

    @FXML
    private ImageView mCLoseInfoPanelIcon;

    @FXML
    public Pane mNotificationsCircle;

    @FXML
    public ImageView mHomeIcon;

    @FXML
    public ImageView mNotificationsIcon;

    // Zmienne do popapu z informacjami o danym userze

    @FXML
    private Pane mUserInfoPanel; // cały panel

    @FXML
    private Label mUserPanelName; // nazwa usera - do niej przypisać dane z backendu

    @FXML
    private Label mUserPanelEmail; // email usera - do niej przypisać dane z backendu

    @FXML
    private Label mUserPanelProjects; // projekty usera - tutaj można dać tablicę stringów z nazwami wszystkich projektów danego usera

    @FXML
    private Label mUserPanelStatus; // status danego użytkownika -> uwaga, przy zmianie statusu pasuje zablokować moźliwość zmiany statusu z tego samego na ten sam (trzeba to obsłużyć)

    @FXML
    private RadioButton mUserPanelAdminStatus; // tu checkboxy do zmiany status, domyślnie jest zaznaczony zwykły użytkownik

    @FXML
    private RadioButton mUserPanelBossStatus;

    @FXML
    private RadioButton mUserPanelCommonUserStatus;

    @FXML
    private Button mUserPanelBtn; // przycisk do zatwierdzenia statusu

    @FXML
    private ImageView mDeleteUserIcon; // ikona do usunięcia profilu -> uwaga (patrz linia 152)

    @FXML
    private ImageView mCloseUserPanelIcon; // ikona do zamknięcia popapu

    @FXML
    private Label mDeleteProfileText; // tekst, po kliknięciu także  usuwa  usera -> uwaga, po kliknięciu trzeba wywołąć popap z warningiem "Czy napewno chcesz usunąć ?", a dopiero po tym wywołać metodę usuwajacą

    @FXML
    public Label mUserPanelEmailLabel;

    @FXML
    public Label mUserPanelProjectsLabel;

    @FXML
    public Label mUserPanelStatusLabel;

    @FXML
    public ToggleGroup mInfoPanelAccountType;

    @FXML
    private ListView<Domain> mUserWorkspacesList;

    @FXML
    public Label mUserAccountDate;


    //Views initialize
    public void initialize() {
        mWelcomeUserName.setText(getUsername());
        mPrivilegeUser.setText(getPrivilege());

        refresh();

        /**
         * Funkcja nasłuchująca, jaki użytkownik na liście został kliknięty. Otiera panel z informacjami o użytkowniku
         */
        mUserlist.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {

            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                mUserInfoPanel.setVisible(true);

                refreshUserInfoPanel(newValue);
            }
        });
    }

    /**
     * Metoda odświeżająca listy domen i użytkowników
     */
    @Override
    public void refresh() {
        RequestService requestService = new RequestService();

        try {
            RsDomains domains = requestService.getUserDomains(getUserId());
            mWorkspaceList.getItems().clear();
            mWorkspaceList.getItems().addAll(domains.getDomains());
            // funkcja nadająca elementom listy klasę css
            mWorkspaceList.setCellFactory(lv -> new ListCell<Domain>() {
                private ImageView image1 = new ImageView();
                private ImageView image2 = new ImageView();
                @Override
                protected void updateItem(Domain domain, boolean empty) {
                    super.updateItem(domain, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(domain.toString());
                    }
                    // nadanie elementowi listy klasy css
                    getStyleClass().add("domain-list-item");
                }
            });
        } catch (IOException e) {
            DialogsUtils.shortErrorDialog("Błąd", "Nie można pobrać listy domen z serwera. Błąd połączenia z serwerem.");
            e.printStackTrace();
        }

        try {
            ResponseObject responseObject = requestService.requestListOfUsers(getUserId());
            mUserlist.getItems().clear();
            mUserlist.getItems().addAll(responseObject.getUsers());

            // funkcja nadająca elementom listy klasę css
            mUserlist.setCellFactory(lv -> new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(user.toString());
                    }
                    // nadanie elementowi listy klasy css
                    getStyleClass().add("user-list-item");
                }
            });
        } catch (IOException e) {
            DialogsUtils.shortErrorDialog("Błąd", "Nie można pobrać listy użytkowników z serwera. Błąd połączenia z serwerem.");
            e.printStackTrace();
        }
    }

    /**
     * Odświeża panel z informacjami o użytkowniku
     * @param user użytkownik, którego dane mają zostać wyświetlone
     */
    private void refreshUserInfoPanel(User user) {
        if (user != null) {
            RequestService requestService = new RequestService();

            int user_id = user.getId();

            RsUserInfo response;
            try {
                response = requestService.getUserInfo(user_id);

                if (response.isSuccess()) {

                    mUserPanelName.setText(response.getName());
                    mUserPanelEmail.setText(response.getEmail());
                    mUserPanelStatus.setText(user.getPrivilege());
                    mUserWorkspacesList.getItems().clear();
                    mUserWorkspacesList.getItems().addAll(response.getDomains());
                    mUserAccountDate.setText(response.getCreated_at());

                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można pobrać informacji o użytkowniku. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        }
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
     * Funkcja czyszcząca pola do tworzenia nowego użytkownika
     */
    public void clearNewUserFields() {
        mEmail.setText("");
        mPassword.setText("");
        mUserName.setText("");
    }

    /**
     * Funkcja czyszcząca pola do tworzenia nowej przestrzeni roboczej
     */
    public void clearNewDomainFields() {
        mWorkspaceName.setText("");
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
        String domain = getDomain();
        String privilege = selectedButton.getText();
        String username = mUserName.getText();
        int user_id = getUserId();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty())
            showErrorPanel("Błąd: Proszę wypełnić wszystkie pola.");
        else if (!RegexUtils.validateEmail(email))
            showErrorPanel("Błąd: Wpisano niepoprawny adres e-mail.");
        else {
            RequestService requestService = new RequestService();
            RequestData requestData = new RequestData(email, password, domain, user_id, privilege, username);

            Gson gson = new Gson();
            String inputJSON = gson.toJson(requestData);
            ResponseObject response;
            try {
                response = requestService.requestCreateNewUser(inputJSON);

                if (response.getMsg().equals("Email alredy exists"))
                    showErrorPanel("Błąd: Użytkownik o takim emailu już istnieje");
                else if (response.isSuccess()) {
                    showInfoPanel("Sukces! Utworzono nowego użytkownika: " + username + ", o uprawnieniach: " + privilege + ".");
                    refresh();
                    clearNewUserFields();
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
        String domain = mWorkspaceName.getText();
        int user_id = getUserId();

        if (domain.isEmpty())
            showErrorPanel("Błąd: Proszę podać nazwę nowej przestrzeni roboczej.");
        else {
            RequestService requestService = new RequestService();
            RequestData requestData = new RequestData(user_id, domain);

            Gson gson = new Gson();
            String inputJSON = gson.toJson(requestData);
            ResponseObject response;
            try {
                response = requestService.requestCreateNewDomain(inputJSON);

                if (response.getMsg().equals("Domain already exists"))
                    showErrorPanel("Błąd: Przestrzeń o takiej nazwie już istnieje.");
                else if (response.isSuccess()) {
                    showInfoPanel("Sukces: Utworzono nową przestrzeń roboczą o nazwie: " + domain + ".");
                    refresh();
                    clearNewDomainFields();
                } else if (!response.isSuccess())
                    DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
            } catch (IOException e) {
                DialogsUtils.shortErrorDialog("Błąd", "Nie można stworzyć nowej przestrzeni. Błąd połączenia z serwerem.");
                e.printStackTrace();
            }
        }
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

    @FXML
    public void closeInfoPanel(MouseEvent event) {
        mInfoPanel.setVisible(false);
    }

    @FXML
    public void acceptInfoPanel(MouseEvent event) {
        mInfoPanel.setVisible(false);
    }

    @FXML // funkcja do zmiany statusu usera w popapie
    void changeThisUserStatus(MouseEvent event) {

    }

    @FXML
    void closeUserInfoPanel(MouseEvent event) {
        mUserInfoPanel.setVisible(false);
    }

    @FXML // funkcja usuwająca usera z bazy
    void deleteThisUser(MouseEvent event) {
        
    }

    /**
     * Funkcja do zmiany uprawnień użytkownika
     * @param actionEvent event
     */
    @FXML
    public void changeUserPrivilage(ActionEvent actionEvent) {
        RadioButton selectedButton = (RadioButton) mInfoPanelAccountType.getSelectedToggle();

        User user = mUserlist.getSelectionModel().getSelectedItem();

        int user_id = getUserId();
        int granted_to = user.getId();
        String domain = getDomain();
        String privilege = selectedButton.getText();

        RequestService requestService = new RequestService();
        RequestData requestData = new RequestData(user_id, granted_to, domain, privilege);

        Gson gson = new Gson();
        String inputJSON = gson.toJson(requestData);
        ResponseObject response;
        try {
            response = requestService.requestUpdatePermission(inputJSON);

            if (response.isSuccess()) {
                showInfoPanel("Zmieniono uprawnienia użytkownika " + user.toString() + " na: " + privilege + ".");
                refresh();
                mUserPanelStatus.setText(privilege);
            } else if (!response.isSuccess())
                DialogsUtils.errorDialog("Błąd", "Błąd z serwera", response.getMsg());
        } catch (IOException e) {
            DialogsUtils.shortErrorDialog("Błąd", "Nie można zmienić uprawnień użytkownikowi. Błąd połączenia z serwerem.");
            e.printStackTrace();
        }
    }
}
