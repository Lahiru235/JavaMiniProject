package controller;

import util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private Label     welcomeLabel;
    @FXML private StackPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        welcomeLabel.setText("Welcome,  " + Session.get().getFullName());
        loadScreen("/view/ManageUsers.fxml");   // default screen
    }

    @FXML public void showManageUsers()   { loadScreen("/view/ManageUsers.fxml"); }
    @FXML public void showManageCourses() { loadScreen("/view/ManageCourses.fxml"); }
    @FXML public void showManageTimetables() { loadScreen("/view/ManageTimetables.fxml"); }
    @FXML public void showNoticeBoard()   { loadScreen("/view/NoticeBoard.fxml"); }

    @FXML
    public void handleLogout() {
        try {
            Session.clear();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/Login.fxml"));
            stage.setScene(new Scene(loader.load(), 480, 360));
            stage.setMaximized(false);
            stage.setTitle("Faculty of Technology — Management System");
            stage.show();
        } catch (Exception e) {
            System.out.println("Logout error: " + e.getMessage());
        }
    }

    private void loadScreen(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent content = loader.load();
            contentArea.getChildren().setAll(content);
        } catch (Exception e) {
            System.out.println("Load error: " + e.getMessage());
        }
    }
}
