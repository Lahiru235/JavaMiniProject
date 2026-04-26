package controller;

import dao.UserDAO;
import model.User;
import util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate empty fields
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password.");
            return;
        }

        try {
            User user = userDAO.login(username, password);

            if (user == null) {
                showError("Invalid username or password.");
                return;
            }

            // Save to session — accessible by all members
            Session.set(user);

            // Open correct dashboard
            String fxml = switch (user.getRole()) {
                case "ADMIN"    -> "/view/AdminDashboard.fxml";
                case "LECTURER" -> "/view/LecturerDashboard.fxml";
                case "STUDENT"  -> "/view/StudentDashboard.fxml";
                case "TECH"     -> "/view/TechDashboard.fxml";
                default -> throw new Exception("Unknown role");
            };

            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(user.getDashboardTitle());
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setStyle("-fx-text-fill: red;");
    }
}
