package controllers;

import backend.CurrentlyLoggedAccount;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Klasa, po której dziedziczą inne kontrolery.
 * Zawiera kilka wspólnych dla wszystkich kontrolerów metod.
 */
public class BaseController {

    private Controller controller;

    public Controller getController() {
        return controller;
    }
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    protected AnchorPane mWrapper;

    @FXML
    protected Pane mMain;

    @FXML
    protected Pane mNavbar;

    @FXML
    protected ImageView mHomeIcon;

    @FXML
    protected Label mWelcomeUserName;

    @FXML
    protected Label mPrivilegeUser;

    @FXML
    protected ImageView mLogoutIcon;

    @FXML
    protected ImageView mNotificationsIcon;

    @FXML
    protected Pane mNotificationsPanel;

    @FXML
    protected Pane mNotificationsCircle;

    @FXML
    protected Label mNotificationsCounter;

    @FXML
    protected ListView<?> mNotificationsList;

    // Zmienne do popapów informacyjnych ->
    @FXML
    protected Pane mInfoPanel;

    @FXML
    protected ImageView mInfoIcon;

    @FXML
    protected Label mTextInfoPanel;

    @FXML
    protected Button mCloseInfoButton;

    @FXML
    protected ImageView mCLoseInfoPanelIcon;


    /**
     * Metoda zwracająca obiekt aktualnie zalogowanego użytkownika.
     * @return obiekt aktualnie zalogowanego użytkownika
     */
    public CurrentlyLoggedAccount getUser() {
        return Controller.currAcc;
    }

    /**
     * Metoda zwracająca id aktualnie zalogowanego użytkownika.
     * @return id aktualnie zalogowanego użytkownika
     */
    public int getUserId() {
        return getUser().getUser_id();
    }

    /**
     * Metoda zwracająca nazwę aktualnie zalogowanego użytkownika.
     * @return nazwę aktualnie zalogowanego użytkownika
     */
    public String getUsername() {
        return getUser().getUsername();
    }

    /**
     * Metoda zwracająca nazwę domeny aktualnie zalogowanego użytkownika.
     * @return nazwę domeny aktualnie zalogowanego użytkownika
     */
    public String getDomain() {
        return getUser().getDomain();
    }

    /**
     * Metoda zwracająca uprawnienia aktualnie zalogowanego użytkownika.
     * @return nazwę uprawnienia aktualnie zalogowanego użytkownika
     */
    public String getPrivilege() {
        return getUser().getPrivilege();
    }


    //Views initialize
    public void initialize() {
        mWelcomeUserName.setText(Controller.currAcc.getUsername());
        mPrivilegeUser.setText(Controller.currAcc.getPrivilege());

        this.refresh();
    }

    public void refresh(){

    }

    @FXML
    protected void logoutUser(MouseEvent event) {
        Controller.currAcc = null;
        getController().clearFields();
        getController().showWindow();
        // get a handle to the stage
        Stage stage = (Stage) mLogoutIcon.getScene().getWindow();
        // do what you have to do
        stage.close();
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

    @FXML
    protected void showNotificationPanel(MouseEvent event) {
        mNotificationsPanel.setVisible(true);
    }

    @FXML
    protected void closeNotificationsPanel(MouseEvent event) {
        mNotificationsPanel.setVisible(false);
    }

    @FXML
    protected void showNotificationsPanel(MouseEvent event) {
        mNotificationsPanel.setVisible(true);
    }

}
