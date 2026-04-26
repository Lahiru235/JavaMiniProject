package controller;

import dao.CourseDAO;
import model.Course;
import model.User;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ManageCoursesController implements Initializable {

    // Table
    @FXML private TableView<Course>            courseTable;
    @FXML private TableColumn<Course,String>   colCode;
    @FXML private TableColumn<Course,String>   colName;
    @FXML private TableColumn<Course,Integer>  colCredits;
    @FXML private TableColumn<Course,String>   colLecturer;

    // Form
    @FXML private TextField      codeField;
    @FXML private TextField      nameField;
    @FXML private TextField      creditsField;
    @FXML private ComboBox<String> lecturerCombo;
    @FXML private Label          statusLabel;

    private final CourseDAO courseDAO = new CourseDAO();
    private List<User>      lecturers;
    private Course          selected  = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colLecturer.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));

        loadLecturers();
        loadCourses();

        // Fill form when row clicked
        courseTable.getSelectionModel().selectedItemProperty()
            .addListener((o, old, c) -> {
                if (c != null) {
                    selected = c;
                    codeField.setText(c.getCourseCode());
                    nameField.setText(c.getCourseName());
                    creditsField.setText(String.valueOf(c.getCredits()));
                    lecturerCombo.setValue(c.getLecturerName());
                }
            });
    }

    private void loadLecturers() {
        try {
            lecturers = courseDAO.getLecturers();
            ObservableList<String> names = FXCollections.observableArrayList();
            names.add("Not Assigned");
            for (User u : lecturers) names.add(u.getFullName());
            lecturerCombo.setItems(names);
            lecturerCombo.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            showStatus("Failed to load lecturers: " + e.getMessage(), true);
        }
    }

    private void loadCourses() {
        try {
            courseTable.setItems(FXCollections.observableArrayList(
                courseDAO.getAll()));
        } catch (SQLException e) {
            showStatus("Failed to load courses: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleNew() {
        selected = null;
        codeField.clear();
        nameField.clear();
        creditsField.clear();
        lecturerCombo.getSelectionModel().selectFirst();
        statusLabel.setText("");
    }

    @FXML
    public void handleSave() {
        String code    = codeField.getText().trim().toUpperCase();
        String name    = nameField.getText().trim();
        String credStr = creditsField.getText().trim();

        if (code.isEmpty() || name.isEmpty() || credStr.isEmpty()) {
            showStatus("All fields are required.", true);
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(credStr);
            if (credits <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showStatus("Credits must be a positive number.", true);
            return;
        }

        // Get lecturer id from selected name
        int lecturerId = 0;
        int idx = lecturerCombo.getSelectionModel().getSelectedIndex();
        if (idx > 0 && lecturers != null)
            lecturerId = lecturers.get(idx - 1).getId();

        try {
            if (selected == null) {
                courseDAO.add(code, name, credits, lecturerId);
                showStatus("Course added.", false);
            } else {
                courseDAO.update(selected.getId(), code, name,
                                 credits, lecturerId);
                showStatus("Course updated.", false);
            }
            loadCourses();
            handleNew();
        } catch (SQLException e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleDelete() {
        if (selected == null) {
            showStatus("Select a course first.", true);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete " + selected.getCourseCode() + "?",
            ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    courseDAO.delete(selected.getId());
                    showStatus("Course deleted.", false);
                    loadCourses();
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
