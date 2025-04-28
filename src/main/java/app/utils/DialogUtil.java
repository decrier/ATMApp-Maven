package app.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;


public class DialogUtil {

    public static void showStyleAlert (AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                DialogUtil.class.getResource("/app/styles/dialogs.css").toExternalForm()
        );

        if (type == AlertType.ERROR) {
            dialogPane.getStyleClass().add("error");
        } else if (type == AlertType.INFORMATION) {
            dialogPane.getStyleClass().add("success");
        }

        alert.showAndWait();
    }
}
