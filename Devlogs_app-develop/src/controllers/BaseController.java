package controllers;

import backend.CurrentlyLoggedAccount;
import backend.responseObjects.Domain;

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

    public void refresh(){

    }
}
