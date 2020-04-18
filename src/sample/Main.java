package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
<<<<<<< HEAD
import javafx.scene.text.Font;
=======
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
>>>>>>> 7b62236d18b8fb2427a6789480fa6d7a85005869
import javafx.stage.Stage;

public class Main extends Application {

    @Override
<<<<<<< HEAD
    public void start(Stage scene) throws Exception {
        Font.loadFont(Main.class.getResource("Righteous.TTF").toExternalForm(), 15);
        Font.loadFont(Main.class.getResource("Lato.TTF").toExternalForm(), 15);
        Parent root = FXMLLoader.load(getClass().getResource("../views/adminView.fxml"));
        scene.setTitle("Devslog");
        scene.setScene(new Scene(root, 1024, 768));
        scene.show();
=======
    public void start(Stage primaryStage) throws Exception{

        Font.loadFont(Main.class.getResource("Righteous.TTF").toExternalForm(), 10);
        Font.loadFont(Main.class.getResource("Lato.TTF").toExternalForm(), 10);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("sample.fxml"));
        Pane pane = loader.load();
        Scene scene = new Scene(pane);

        primaryStage.setScene(scene);
        primaryStage.show();
>>>>>>> 7b62236d18b8fb2427a6789480fa6d7a85005869
    }


    public static void main(String[] args) {
        launch(args);
<<<<<<< HEAD
    }
}
=======

    }


    }

>>>>>>> 7b62236d18b8fb2427a6789480fa6d7a85005869
