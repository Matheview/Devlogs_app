package components;

import backend.dataObjects.User;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

/**
 * Awatar użytkownika
 */
public class UserAvatar extends Label {
    private User user;

    public UserAvatar() {
        super();
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("user-circle");

        setUser(null);
    }

    public UserAvatar(User user) {
        super();
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("user-circle");

        setUser(user);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user != null) {
            this.setText(user.getShortcut());
        } else {
            this.setText("");
        }
        this.user = user;
    }

    public String getUserName() {
        return getUser().getName();
    }

}
