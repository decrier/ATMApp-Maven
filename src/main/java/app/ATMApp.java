package app;

import app.utils.CreatedBy;
import app.utils.I18n;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.lang.Override;
import java.util.Locale;
import java.util.ResourceBundle;

@CreatedBy(author = "Mykola Havrysh", theme = "Geldautomat: Java, JavaFX, MariaDB")
public class ATMApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", I18n.getLocale());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), bundle);
        Parent root = loader.load();
        primaryStage.setTitle(bundle.getString("title"));
        primaryStage.setScene(new Scene(root, 500, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}