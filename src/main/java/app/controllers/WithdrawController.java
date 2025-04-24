package app.controllers;

import app.Session;
import app.database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mariadb.jdbc.client.result.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WithdrawController {

    @FXML
    private TextField amountField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleWithdraw() {
        String input = amountField.getText().trim();

        try {
            double amount = Double.parseDouble(input);

            if (amount <= 0) {
                statusLabel.setText("Geben Sie eine positive Zahl!");
                return;
            }

            try (Connection conn = Database.connect()) {
                conn.setAutoCommit(false);

                String checkSql = "SELECT balance FROM users WHERE card_number = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, Session.getCardNumber());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    double balance = rs.getDouble("balance");

                    if (amount <= balance) {
                        double newBalance = balance - amount;

                        String updateSql = "UPDATE users SET balance = ? WHERE card_number = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setDouble(1, newBalance);
                        updateStmt.setString(2, Session.getCardNumber());
                        updateStmt.executeUpdate();

                        String insertTransaction = "INSERT INTO transactions " +
                                "(card_number, amount, transaction_type) VALUES (?, ?, ?)";
                        PreparedStatement transStmt = conn.prepareStatement(insertTransaction);
                        transStmt.setString(1, Session.getCardNumber());
                        transStmt.setDouble(2, amount);
                        transStmt.setString(3, "withdraw");
                        transStmt.executeUpdate();

                        conn.commit();

                        statusLabel.setText("Auszahlung von " + amount +
                                "€ erfolgreich.\nNeuer Kontostand: " + newBalance + "€");
                        amountField.clear();
                    } else {
                        statusLabel.setText("Nicht genugend Geld auf dem Konto.");
                    }
                } else {
                    statusLabel.setText("Benutzer nicht gefunden.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                statusLabel.setText("Fehler beim Zugriff auf die Datenbank");
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Ungültiger Betrag.");
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
            statusLabel.setText("Fehler beim zurückkehren zum Menü.");
        }
    }
}
