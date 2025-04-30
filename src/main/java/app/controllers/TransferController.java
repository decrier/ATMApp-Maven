package app.controllers;

import app.Session;
import app.database.Database;
import app.utils.I18n;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TransferController {

    @FXML
    private TextField recipientCardField;

    @FXML
    private TextField amountField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleTransfer() {
        String recipient = recipientCardField.getText().trim();
        String amountText = amountField.getText().trim();

        if (recipient.isEmpty() || amountText.isEmpty()) {
            statusLabel.setText(I18n.get("error.empty-field"));
            return;
        }

        double amount;
        String sender = Session.getCardNumber();
        try {
            amount = Double.parseDouble(amountText);
            if (recipient.equals(sender)) {
                statusLabel.setText(I18n.get("error.transfer-to-yourself"));
                return;
            }

            try (Connection conn = Database.connect()){
                // Kontostand des Senders
                String sndBalanceSql = "SELECT balance FROM users WHERE card_number = ?";
                PreparedStatement ps1 = conn.prepareStatement(sndBalanceSql);
                ps1.setString(1, sender);
                ResultSet rs1 = ps1.executeQuery();
                if (!rs1.next()) {
                    statusLabel.setText(I18n.get("error.sender-not-found"));
                    conn.rollback();
                    return;
                }

                double sndBalance = rs1.getDouble("balance");
                if (amount > sndBalance) {
                    statusLabel.setText(I18n.get("error.not-enough-money"));
                    conn.rollback();
                    return;
                }

                //Überprüfung des Empfängers und seines Guthabens
                String rcpBalanceSql = "SELECT balance FROM users WHERE card_number = ? FOR UPDATE";
                PreparedStatement ps2 = conn.prepareStatement(rcpBalanceSql);
                ps2.setString(1, recipient);
                ResultSet rs2 = ps2.executeQuery();
                if (!rs2.next()) {
                    statusLabel.setText(I18n.get("error.recipient-not-found"));
                    conn.rollback();
                    return;
                }

                double rcpBalance = rs2.getDouble("balance");

                // Update von Balance
                String sndBalanceUpd = "UPDATE users SET balance = ? WHERE card_number = ?";
                PreparedStatement sndUpd = conn.prepareStatement(sndBalanceUpd);
                sndUpd.setDouble(1, sndBalance - amount);
                sndUpd.setString(2, sender);
                sndUpd.executeUpdate();

                String rcpUpdateSql = "UPDATE users SET balance = ? WHERE card_number = ?";
                PreparedStatement rcpUpd = conn.prepareStatement(rcpUpdateSql);
                rcpUpd.setDouble(1,rcpBalance + amount);
                rcpUpd.setString(2, recipient);

                // Aufzeichnen von Transaktionen
                String sndTransSql = "INSERT INTO transactions (card_number, amount, transaction_type, timestamp) " +
                                "VALUES (?, ?, ?, NOW())";
                PreparedStatement sndTransUpd = conn.prepareStatement(sndTransSql);
                sndTransUpd.setString(1, sender);
                sndTransUpd.setDouble(2, -amount);
                sndTransUpd.setString(3, "transfer_sent");
                sndTransUpd.executeQuery();

                String rcpTransSql = "INSERT INTO transactions (card_number, amount, transaction_type, timestamp) " +
                                "VALUES (?, ?, ?, NOW())";
                PreparedStatement rcpTransUpd = conn.prepareStatement(rcpTransSql);
                rcpTransUpd.setString(1, recipient);
                rcpTransUpd.setDouble(2, amount);
                rcpTransUpd.setString(3, "transfer_received");
                rcpTransUpd.executeUpdate();

                conn.commit();
                statusLabel.setText(I18n.get("transfer.success"));

            } catch (SQLException e) {
                e.printStackTrace();
                statusLabel.setText(I18n.get("error.transer"));
            }
        } catch (NumberFormatException e) {
            statusLabel.setText(I18n.get("error.invalid-amount"));
        }

    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"),
                    ResourceBundle.getBundle("i18n.messages", I18n.getLocale()));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(I18n.get("title.main-menu"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
