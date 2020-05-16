package backend.responseObjects;

import java.util.List;

// Klasa w której przechowywana jest odpowiedź serwera na zopytanie
// o pobranie listy projektów
public class RsProjects extends BaseResponseObject {
    private List<Project> projects;

    public List<Project> getProjects() {
        return projects;
    }
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "RsProjects{" +
                "msg='" + super.getMsg() + '\'' +
                ", projects=" + projects +
                ", success='" + super.isSuccess() + '\'' +
                '}';
    }
}
