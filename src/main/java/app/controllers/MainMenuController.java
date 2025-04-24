package app.controllers;

import app.Session;
import app.database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainMenuController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private void initialize() {
        welcomeLabel.setText("Hallo " + Session.getFullName());
        balanceLabel.setText("Ihr Kontostand: Klicken Sie oben auf die Taste");
    }

    @FXML
    public void showBalance() {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT balance FROM users WHERE card_number = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, Session.getCardNumber());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                balanceLabel.setText("Ihr Kontostand: " + balance + " €");
            }
        } catch (SQLException e) {
            balanceLabel.setText("Fehler beim Abrufen des Kontostands.");
        }
    }
    

    @FXML
    private void goToWithdraw(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/withdraw.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Geldauszahlung");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeposit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/deposit.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Einzahlung");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Session.clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("ATM Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showTransactions(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transactions.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
