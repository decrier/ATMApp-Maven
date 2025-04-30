package app.controllers;

import app.Session;
import app.database.Database;
import app.utils.I18n;
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
import java.text.MessageFormat;
import java.util.ResourceBundle;

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
                statusLabel.setText(I18n.get("message.input-positive"));
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

                        statusLabel.setText(MessageFormat.format(I18n.get("withdraw.success"), amount, newBalance));
                        amountField.clear();
                    } else {
                        statusLabel.setText(I18n.get("error.not-enough-money"));
                    }
                } else {
                    statusLabel.setText(I18n.get("message.user-not-found"));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                statusLabel.setText(I18n.get("error.db-connect"));
            }

        } catch (NumberFormatException e) {
            statusLabel.setText(I18n.get("error.invalid-amount"));
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"),
                    ResourceBundle.getBundle("i18n.messages", I18n.getLocale()));
            Scene scene = new Scene(loader.load());

            MainMenuController controller = loader.getController();
            controller.showBalance();

            Stage stage = (Stage) amountField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(I18n.get("title.main-menu"));
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText(I18n.get("error.return-to-menu"));
        }
    }
}
