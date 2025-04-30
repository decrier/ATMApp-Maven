package app.controllers;

import app.Session;
import app.database.Database;
import app.utils.DialogUtil;
import app.utils.I18n;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;


public class LoginController {

    @FXML
    private TextField cardNumberField;

    @FXML
    private PasswordField pinField;

    @FXML
    private ComboBox<String> languageSelector;

    @FXML
    private void handleLogin() {
        String cardNumber = cardNumberField.getText().trim();
        String pin = pinField.getText().trim();

        if(cardNumber.isEmpty() || pin.isEmpty()) {
            DialogUtil.showStyleAlert(AlertType.ERROR, I18n.get("error"), I18n.get("error.empty"));
        }

        if(!cardNumber.matches("\\d{8,16}") || !pin.matches("\\d{4}")) {
            DialogUtil.showStyleAlert(AlertType.ERROR, I18n.get("error"), I18n.get("error.invalid"));
            return;
        }

        try (Connection conn = Database.connect()) {
            String query = "SELECT full_name FROM users WHERE card_number = ? AND pin = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, cardNumber);
            stmt.setString(2, pin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("full_name");
                DialogUtil.showStyleAlert(AlertType.INFORMATION, I18n.get("message"), I18n.get("welcome") + name + "!");
                Session.setUser(name, cardNumber);

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"),
                            ResourceBundle.getBundle("i18n.messages", I18n.getLocale()));
                    Scene scene = new Scene(loader.load());
                    Stage stage = (Stage) cardNumberField.getScene().getWindow();
                    stage.setScene(scene);
                    stage.setTitle(I18n.get("title.main-menu"));
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtil.showStyleAlert(AlertType.ERROR, I18n.get("error"), I18n.get("error.main-menu-load"));
                }

            } else {
                DialogUtil.showStyleAlert(AlertType.ERROR, I18n.get("error"), I18n.get("error.auth"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DialogUtil.showStyleAlert(AlertType.ERROR, I18n.get("error"), I18n.get("error.db-connect"));
        }

    }

    @FXML
    private void initialize() {
        languageSelector.getItems().addAll(
                "Deutsch",
                "English",
                "Русский"
        );

        String curruntLanguage = I18n.getLanguageDisplayName();
        languageSelector.getSelectionModel().select(curruntLanguage);
    }

    @FXML
    private void handleLanguageChange() {
        String selected = languageSelector.getValue();

        if (selected.equals(I18n.get("language.select"))) {
            return;
        }

        switch(selected) {
            case "Deutsch":
                I18n.setLocale(Locale.GERMAN);
                break;
            case "English":
                I18n.setLocale(Locale.ENGLISH);
                break;
            case "Русский":
                I18n.setLocale(new Locale("ru"));
                break;
            default:
                return;
        }

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", I18n.getLocale());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), bundle);
            Parent root = loader.load();

            Stage stage = (Stage) languageSelector.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(bundle.getString("title"));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
     */
}
