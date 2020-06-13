package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Font.loadFont(Main.class.getResource("../fonts/Righteous.TTF").toExternalForm(), 10);
        Font.loadFont(Main.class.getResource("../fonts/Lato.TTF").toExternalForm(), 10);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("../views/login.fxml"));

        Pane pane = loader.load();
        Scene scene = new Scene(pane);

        primaryStage.setTitle(AppInfo.TITLE + " - logowanie");
        primaryStage.getIcons().add(AppInfo.ICON);
        primaryStage.setScene(scene);
        primaryStage.show();

    }


}

