package components;

import backend.dataObjects.Comment;
import backend.dataObjects.User;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * Panel zawierający wygląd komentarza
 */
public class CommentPane extends Pane {
    private Comment comment;
    private List<User> usersList;
    private UserAvatar avatar;
    private Label authorNameLabel;
    private Label contentLabel;

    /**
     * Konstrukto klasy CommentPane
     * @param comment obiekt komentarza
     * @param usersList lista, z której CommentPane pobiera informacje o userze
     */
    public CommentPane(Comment comment, List<User> usersList) {
        super();

        this.setPadding(new Insets(10, 10, 10, 10));

        setAvatar(new UserAvatar());
        setAuthorNameLabel(new Label());
        setContentLabel(new Label());
        setUsersList(usersList);
        setComment(comment);
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;

        if (getComment() != null) {
            User user = getAuthor();
            if (user != null) {
                getAvatar().setUser(user);
                getAuthorNameLabel().setText(user.getName());
            } else {
                getAvatar().setUser(null);
                getAvatar().setText("?");
                getAuthorNameLabel().setText("Nieznany użytkownik");
            }
            getContentLabel().setText(comment.getComment_desc());
        } else {
            getAvatar().setUser(null);
            getAuthorNameLabel().setText("");
            getContentLabel().setText("");
        }
    }

    public List<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }

    public UserAvatar getAvatar() {
        return avatar;
    }

    private void setAvatar(UserAvatar avatar) {
        if (getAvatar() != null)
            this.getChildren().remove(this.avatar);

        this.avatar = avatar;
        getAvatar().setLayoutX(10.0);
        getAvatar().setLayoutY(20.0);
        this.getChildren().add(this.avatar);
    }

    public Label getAuthorNameLabel() {
        return authorNameLabel;
    }

    private void setAuthorNameLabel(Label authorNameLabel) {
        if (getAuthorNameLabel() != null)
            this.getChildren().remove(this.authorNameLabel);

        this.authorNameLabel = authorNameLabel;
        getAuthorNameLabel().setLayoutX(60.0);
        getAuthorNameLabel().setLayoutY(15.0);
        getAuthorNameLabel().getStyleClass().add("comment-author");
        this.getChildren().add(this.authorNameLabel);
    }

    public Label getContentLabel() {
        return contentLabel;
    }

    private void setContentLabel(Label contentLabel) {
        if (getContentLabel() != null)
            this.getChildren().remove(this.contentLabel);

        this.contentLabel = contentLabel;
        getContentLabel().setLayoutX(60.0);
        getContentLabel().setLayoutY(40.0);
        getContentLabel().getStyleClass().add("comment-content");
        this.getChildren().add(this.contentLabel);
    }

    /**
     * Funkcja przeszukująca listę użytkowników i zwracająca dane autora komentarza,
     * lub null, w przypadku nie znalezienia go na liście
     * @return dane autora komentarza lub null, w przypadku nie znalezienia go na liście
     */
    public User getAuthor() {
        return User.getUserFromListById(comment.getUser_id(), getUsersList());
    }
}
