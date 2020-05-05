package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class DialogsUtils {

    /** Okno dialogowe informacyjne */
    public static void infoDialog(String title, String header, String text) {
        Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
        informationAlert.setTitle(title);
        informationAlert.setHeaderText(header);
        informationAlert.setContentText(text);

        informationAlert.showAndWait();
    }


    /** Okno dialogowe błędów */
    public static void errorDialog(String title, String header, String text) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(header);

        TextArea textArea = new TextArea(text);
        errorAlert.getDialogPane().setContent(textArea);

        errorAlert.showAndWait();
    }

    /** Okno dialogowe błędów bez opisu*/
    public static void shortErrorDialog(String title, String header) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(header);

        errorAlert.showAndWait();
    }
}
