package backend.responseObjects;

import java.util.List;

public class rsProjects extends BaseResponseObject {
    private List<Project> projects;

    public List<Project> getProjects() {
        return projects;
    }
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "rsProjects{" +
                "msg='" + super.getMsg() + '\'' +
                ", projects=" + projects +
                ", success='" + super.isSuccess() + '\'' +
                '}';
    }
}
