package app.controllers;

import app.Session;
import app.database.Database;
import app.utils.DialogUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class ChangePinController {

    @FXML
    private PasswordField enteredPin;

    @FXML
    private PasswordField newInputPin1;

    @FXML
    private PasswordField newInputPin2;

    @FXML
    private Label statusLabel;

    @FXML
    public void changePin(ActionEvent event) {
        String inputPin = enteredPin.getText().trim();
        String newPin1 = newInputPin1.getText().trim();
        String newPin2 = newInputPin2.getText().trim();

        try(Connection conn = Database.connect()) {
            String cardNumber = Session.getCardNumber();
            PreparedStatement pinRequest = conn.prepareStatement("SELECT pin FROM users WHERE card_number = ? ");
            pinRequest.setString(1, cardNumber);
            ResultSet rs = pinRequest.executeQuery();

            if (rs.next()) {
                String pinSql = rs.getString("pin");
                if (!pinSql.equals(inputPin)) {
                    DialogUtil.showStyleAlert(AlertType.ERROR, "Fehler", "Falsche PIN eingegeben");
                    enteredPin.clear();
                    return;
                }

                if (inputPin.isEmpty() || newPin1.isEmpty() || newPin2.isEmpty()) {
                    DialogUtil.showStyleAlert(AlertType.ERROR, "Fehler", "Felder dürfen nicht leer sein!");
                    return;
                }

                if (!inputPin.matches("\\d{4}")) {
                    DialogUtil.showStyleAlert(AlertType.ERROR, "Fehler", "Ungültiges PIN-Format! (Im Feld \"Aktuelle PIN\")");
                    return;
                }
                if (!newPin1.matches("\\d{4}"))  {
                    DialogUtil.showStyleAlert(AlertType.ERROR, "Fehler", "Ungültiges PIN-Format! (Im Feld \"Neue PIN\")");
                    return;
                }
                if (!newPin2.matches("\\d{4}")) {
                    DialogUtil.showStyleAlert(AlertType.ERROR, "Fehler", "Ungültiges PIN-Format! (Im Feld \"Neue PIN\" wiederholen)");
                    return;
                }

                if (inputPin.equals(newPin1) || inputPin.equals(newPin2)) {
                    DialogUtil.showStyleAlert(AlertType.ERROR, "Fehler", "Aktuelle und neue PINs dürfen nicht übereinstimmen");
                    return;
                }

                if (!newPin1.equals(newPin2)) {
                    DialogUtil.showStyleAlert(AlertType.ERROR, "Fehler", "Die wiederholte PIN stimmt nicht überein!");
                    return;
                }

                PreparedStatement pinUpd = conn.prepareStatement("UPDATE users SET pin = ? WHERE card_number = ?");
                pinUpd.setString(1, newPin1);
                pinUpd.setString(2, cardNumber);
                pinUpd.executeUpdate();

                conn.commit();

                DialogUtil.showStyleAlert(AlertType.INFORMATION, "Message", "PIN erfolgreich geändert.\nBitte loggen Sie mit Ihrer neuen PIN ein!");
                handleLogout(event);
            } else {
                DialogUtil.showStyleAlert(AlertType.ERROR, "Fehler", "PIN nicht gefunden");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Fehler beim Zugriff auf Datenbank");
        }
    }

    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("ATM - Main Menu");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleLogout(ActionEvent event) {
        Session.clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Geldautomat - Start");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
