package controllers;

import backend.ResponseObject;

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
     * Metoda zwracająca id aktualnie zalogowanego użytkownika.
     * @return id aktualnie zalogowanego użytkownika
     */
    public int getUserId() {
        return Controller.currAcc.getUser_id();
    }

    public void refresh(){

    }
}
