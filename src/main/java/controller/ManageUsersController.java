package controller;

import dao.UserDAO;
import model.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ManageUsersController implements Initializable {

    // Table
    @FXML private TableView<User>            userTable;
    @FXML private TableColumn<User,Integer>  colId;
    @FXML private TableColumn<User,String>   colName;
    @FXML private TableColumn<User,String>   colUsername;
    @FXML private TableColumn<User,String>   colRole;
    @FXML private TableColumn<User,String>   colEmail;

    // Filter
    @FXML private ComboBox<String> roleFilter;

    // Form fields
    @FXML private TextField     fullNameField;
    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     emailField;
    @FXML private TextField     phoneField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField     deptField;
    @FXML private Label         deptLabel;
    @FXML private Label         statusLabel;

    private final UserDAO userDAO = new UserDAO();
    private List<User>    allUsers;
    private User          selected = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Setup filter
        roleFilter.setItems(FXCollections.observableArrayList(
            "ALL","ADMIN","LECTURER","STUDENT","TECH"));
        roleFilter.setValue("ALL");

        // Setup role combo
        roleCombo.setItems(FXCollections.observableArrayList(
            "ADMIN","LECTURER","STUDENT","TECH"));

        // Show/hide dept field based on role
        roleCombo.valueProperty().addListener((o, old, val) -> {
            boolean show = val != null &&
                           (val.equals("LECTURER") || val.equals("STUDENT")
                            || val.equals("TECH"));
            deptLabel.setVisible(show);
            deptField.setVisible(show);
        });

        loadUsers();

        // Fill form when row clicked
        userTable.getSelectionModel().selectedItemProperty()
            .addListener((o, old, user) -> {
                if (user != null) {
                    selected = user;
                    fullNameField.setText(user.getFullName());
                    usernameField.setText(user.getUsername());
                    passwordField.clear();
                    emailField.setText(user.getEmail());
                    phoneField.setText(user.getPhone());
                    roleCombo.setValue(user.getRole());
                    deptField.setText(user.getDept() != null ? user.getDept() : "");
                }
            });
    }

    private void loadUsers() {
        try {
            allUsers = userDAO.getAllUsers();
            applyFilter();
        } catch (SQLException e) {
            showStatus("Failed to load: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleFilter() { applyFilter(); }

    private void applyFilter() {
        String filter = roleFilter.getValue();
        ObservableList<User> filtered = FXCollections.observableArrayList();
        for (User u : allUsers) {
            if (filter.equals("ALL") || u.getRole().equals(filter))
                filtered.add(u);
        }
        userTable.setItems(filtered);
    }

    @FXML
    public void handleNew() {
        selected = null;
        fullNameField.clear();
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        phoneField.clear();
        deptField.clear();
        roleCombo.getSelectionModel().clearSelection();
        statusLabel.setText("");
    }

    @FXML
    public void handleSave() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email    = emailField.getText().trim();
        String phone    = phoneField.getText().trim();
        String role     = roleCombo.getValue();
        String dept     = deptField.getText().trim();
        if ("ADMIN".equals(role)) dept = null;

        // Basic validation
        if (fullName.isEmpty() || username.isEmpty()
            || email.isEmpty() || role == null) {
            showStatus("Name, username, email and role are required.", true);
            return;
        }

        try {
            if (selected == null) {
                // New user — password required
                if (password.isEmpty()) {
                    showStatus("Password is required for new users.", true);
                    return;
                }
                if (userDAO.usernameExists(username)) {
                    showStatus("Username already exists.", true);
                    return;
                }
                userDAO.addUser(username, password, fullName,
                                email, phone, role, dept);
                showStatus("User added successfully.", false);
            } else {
                // Update existing
                if (userDAO.usernameExistsForOtherUser(username, selected.getId())) {
                    showStatus("Username already exists.", true);
                    return;
                }

                boolean updated = userDAO.updateUser(selected.getId(),
                                   username, password, fullName,
                                   email, phone, role, dept);

                if (!updated) {
                    showStatus("User not found for update.", true);
                    return;
                }
                showStatus("User updated successfully.", false);
            }
            loadUsers();
            handleNew();
        } catch (SQLException e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleDelete() {
        if (selected == null) {
            showStatus("Select a user first.", true);
            return;
        }

        try {
            if (userDAO.isPrimaryAdmin(selected.getId())) {
                showStatus("Cannot delete the main admin account.", true);
                return;
            }
        } catch (SQLException e) {
            showStatus("Error: " + e.getMessage(), true);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete " + selected.getFullName() + "?",
            ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    boolean deleted = userDAO.deleteUser(selected.getId());
                    if (!deleted) {
                        showStatus("Cannot delete the main admin account.", true);
                        return;
                    }
                    showStatus("User deleted.", false);
                    loadUsers();
                    handleNew();
                } catch (SQLException e) {
                    showStatus("Error: " + e.getMessage(), true);
                }
            }
        });
    }

    private void showStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        statusLabel.setStyle(error
            ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }
}
