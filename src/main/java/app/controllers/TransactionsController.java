package app.controllers;

import app.Session;
import app.database.Database;
import app.utils.I18n;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

public class TransactionsController {

    @FXML
    private ListView<String> transactionList;

    @FXML
    public void initialize() {
        ObservableList<String> items = FXCollections.observableArrayList();

        try (Connection conn = Database.connect()) {
            String sql = "SELECT amount, transaction_type, timestamp FROM transactions WHERE card_number = ? ORDER BY timestamp DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, Session.getCardNumber());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double amount = rs.getDouble("amount");
                String type = rs.getString("transaction_type");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                String entry = String.format("[%s] %s: %.2f €", timestamp.toLocalDateTime(), type, amount);
                items.add(entry);
            }

        } catch (SQLException e) {
            items.add("Fehler beim Laden der Transaktionen.");
        }

        transactionList.setItems(items);
    }

    @FXML
    private void goBack(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"),
                    ResourceBundle.getBundle("i18n.messages", I18n.getLocale()));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}