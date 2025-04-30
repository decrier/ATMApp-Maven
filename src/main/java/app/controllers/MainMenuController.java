package app.controllers;

import app.Session;
import app.database.Database;
import app.utils.I18n;
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
import java.util.ResourceBundle;

public class MainMenuController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private void initialize() {
        welcomeLabel.setText(I18n.get("hello") + Session.getFullName());
        balanceLabel.setText(I18n.get("balancePrompt"));
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
                if (balance >= 0) {
                    balanceLabel.getStyleClass().add("positive");
                } else {
                    balanceLabel.getStyleClass().add("negative");
                }
                balanceLabel.setText(I18n.get("balance") + balance + " €");
            }
        } catch (SQLException e) {
            balanceLabel.setText(I18n.get("error.balance-retrieval"));
        }
    }

    @FXML
    private void goToWithdraw(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/withdraw.fxml"),
                                    ResourceBundle.getBundle("i18n.messages", I18n.getLocale()));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(I18n.get("cash-withdrawal"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeposit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/deposit.fxml"),
                            ResourceBundle.getBundle("i18n.messages", I18n.getLocale()));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(I18n.get("deposit"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Session.clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"),
                    ResourceBundle.getBundle("i18n.messages", I18n.getLocale()));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(I18n.get("login.title"));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showTransactions(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transactions.fxml"),
                    ResourceBundle.getBundle("i18n.messages", I18n.getLocale()));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToTransfer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transfer.fxml"),
                    ResourceBundle.getBundle("i18n.messages", I18n.getLocale()));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(I18n.get("money-transfer"));
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToPinChange(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/change_pin.fxml"),
                    ResourceBundle.getBundle("i18n.messages", I18n.getLocale()));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(I18n.get("change-pin"));
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
