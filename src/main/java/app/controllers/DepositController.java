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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

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
                statusLabel.setText(I18n.get("message.input-positive"));
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

                    statusLabel.setText(MessageFormat.format(I18n.get("deposit.success"), amount, "\n", newBalance));
                    amountField.clear();
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
