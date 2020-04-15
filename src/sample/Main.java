package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage scene) throws Exception {
        Font.loadFont(Main.class.getResource("Righteous.TTF").toExternalForm(), 10);
        Font.loadFont(Main.class.getResource("Lato.TTF").toExternalForm(), 10);
        Parent root = FXMLLoader.load(getClass().getResource("../views/adminView.fxml"));
        scene.setTitle("Devslog");
        scene.setScene(new Scene(root, 1024, 768));
        scene.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
