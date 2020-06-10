package backend.dataObjects;

public class Notifications {

    private String description;
    private int id;
    private String created_at;
    private boolean readed;

    public Notifications(String description, int id, String created_at, boolean readed) {
        this.description = description;
        this.id = id;
        this.created_at = created_at;
        this.readed = readed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }
}
