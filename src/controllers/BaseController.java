package controllers;

import backend.ResponseObject;

/**
 * Klasa, po której dziedziczą inne kontrolery.
 * Zawiera kilka wspólnych dla wszystkich kontrolerów metod.
 */
public class BaseController {

    private ResponseObject user;

    // Metody do pobierania referencji na obiekt użytkownika (ResponseObject)
    public ResponseObject getUser() {
        return user;
    }
    public void setUser(ResponseObject user) {
        this.user = user;
    }

}
