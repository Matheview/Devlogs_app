package backend.responseObjects;

import backend.dataObjects.Domain;

import java.util.List;

// Klasa w której przechowywana jest odpowiedź serwera na zopytanie
// o pobranie listy domen
public class RsDomains extends BaseResponseObject {
    private List<Domain> domains;

    public List<Domain> getDomains() {
        return domains;
    }
    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }
}
