package app.controllers;

import app.Session;
import app.database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginController {

    @FXML
    private TextField cardNumberField;

    @FXML
    private PasswordField pinField;

    @FXML
    private void handleLogin() {
        String cardNumber = cardNumberField.getText().trim();
        String pin = pinField.getText().trim();

        if(cardNumber.isEmpty() || pin.isEmpty()) {
            showAlert(AlertType.ERROR, "Felder dürfen nicht leer sein.");
        }

        if(!cardNumber.matches("\\d{8,16}") || !pin.matches("\\d{4}")) {
            showAlert(AlertType.ERROR, "Ungültige Kartennummer oder ungültiges PIN-Format.");
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
                showAlert(AlertType.INFORMATION, "Willkommen " + name + "!");

                Session.setUser(name, cardNumber);

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = (Stage) cardNumberField.getScene().getWindow();
                    stage.setScene(scene);
                    stage.setTitle("Main Menu");
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Fehler beim Laden des Hauptmenüs.");
                }

            } else {
                showAlert(AlertType.ERROR, "Ungültige Kartennummer oder ungültiges PIN.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Fehler bei der Datenbankverbindung");
        }

    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
