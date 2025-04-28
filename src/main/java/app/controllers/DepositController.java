package app.controllers;

import app.Session;
import app.database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepositController {
    @FXML
    private TextField amountField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleDeposit() {
        String input = amountField.getText().trim();

        try {
            double amount = Double.parseDouble(input);

            if (amount <= 0) {
                statusLabel.setText("Geben Sie eine positive Zahl!");
                return;
            }

            try (Connection conn = Database.connect()){
                conn.setAutoCommit(false);

                String chkSql = "SELECT balance FROM users WHERE card_number = ?";
                PreparedStatement chkStmt = conn.prepareStatement(chkSql);
                chkStmt.setString(1, Session.getCardNumber());
                ResultSet rs = chkStmt.executeQuery();

                if(rs.next()) {
                    double balance = rs.getDouble("balance");
                    double newBalance = balance + amount;

                    String updSql = "UPDATE users SET balance = ? WHERE card_number = ?";
                    PreparedStatement updSqlStmt = conn.prepareStatement(updSql);
                    updSqlStmt.setDouble(1, newBalance);
                    updSqlStmt.setString(2, Session.getCardNumber());
                    updSqlStmt.executeUpdate();

                    String updTransac = "INSERT INTO transactions " +
                            "(card_number, amount, transaction_type) VALUES (?, ?, ?)";
                    PreparedStatement updTransacStmt = conn.prepareStatement(updTransac);
                    updTransacStmt.setString(1, Session.getCardNumber());
                    updTransacStmt.setDouble(2, amount);
                    updTransacStmt.setString(3, "deposit");
                    updTransacStmt.executeUpdate();

                    conn.commit();

                    statusLabel.setText("Einzahlung von " + amount + "€ erfolgreich. \n" +
                            "Neuer Kontostand: " + newBalance + "€");
                    amountField.clear();
                } else {
                    statusLabel.setText("Benutzer nicht gefunden");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                statusLabel.setText("Fehler beim Zugriff auf die Datenbank");
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Ungültiger Betrag");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            Scene scene = new Scene(loader.load());

            MainMenuController controller = loader.getController();
            controller.showBalance();

            Stage stage = (Stage) amountField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Main Menu");

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Fehler beim Zurückkehren zum Menu");
        }
    }

}
