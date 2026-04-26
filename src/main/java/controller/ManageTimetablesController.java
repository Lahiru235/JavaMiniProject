package controller;

import dao.TimetableDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Timetable;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManageTimetablesController implements Initializable {

    @FXML private TableView<Timetable>           timetableTable;
    @FXML private TableColumn<Timetable,String>  colDept;
    @FXML private TableColumn<Timetable,String>  colCourse;
    @FXML private TableColumn<Timetable,String>  colDay;
    @FXML private TableColumn<Timetable,String>  colStart;
    @FXML private TableColumn<Timetable,String>  colEnd;
    @FXML private TableColumn<Timetable,String>  colVenue;
    @FXML private TableColumn<Timetable,String>  colNote;

    @FXML private TextField      deptField;
    @FXML private TextField      courseField;
    @FXML private ComboBox<String> dayCombo;
    @FXML private TextField      startField;
    @FXML private TextField      endField;
    @FXML private TextField      venueField;
    @FXML private TextField      noteField;
    @FXML private Label          statusLabel;

    private final TimetableDAO timetableDAO = new TimetableDAO();
    private Timetable selected;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colDept.setCellValueFactory(new PropertyValueFactory<>("dept"));
        colCourse.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colDay.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colVenue.setCellValueFactory(new PropertyValueFactory<>("venue"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        dayCombo.setItems(FXCollections.observableArrayList(
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        ));

        timetableTable.getSelectionModel().selectedItemProperty().addListener((o, old, t) -> {
            if (t == null) return;
            selected = t;
            deptField.setText(t.getDept());
            courseField.setText(t.getCourseCode());
            dayCombo.setValue(t.getDayOfWeek());
            startField.setText(t.getStartTime());
            endField.setText(t.getEndTime());
            venueField.setText(t.getVenue());
            noteField.setText(t.getNote() == null ? "" : t.getNote());
        });

        loadRows();
    }

    private void loadRows() {
        try {
            timetableTable.setItems(FXCollections.observableArrayList(timetableDAO.getAll()));
        } catch (SQLException e) {
            showStatus("Failed to load timetables: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleNew() {
        selected = null;
        deptField.clear();
        courseField.clear();
        dayCombo.getSelectionModel().clearSelection();
        startField.clear();
        endField.clear();
        venueField.clear();
        noteField.clear();
        statusLabel.setText("");
    }

    @FXML
    public void handleSave() {
        String dept = deptField.getText().trim();
        String course = courseField.getText().trim().toUpperCase();
        String day = dayCombo.getValue();
        String start = startField.getText().trim();
        String end = endField.getText().trim();
        String venue = venueField.getText().trim();
        String note = noteField.getText().trim();

        if (dept.isEmpty() || course.isEmpty() || day == null
                || start.isEmpty() || end.isEmpty() || venue.isEmpty()) {
            showStatus("Department, course, day, time and venue are required.", true);
            return;
        }

        if (!isTime(start) || !isTime(end)) {
            showStatus("Use HH:mm format for start/end time.", true);
            return;
        }

        try {
            if (selected == null) {
                timetableDAO.add(dept, course, day, start, end, venue, note);
                showStatus("Timetable entry added.", false);
            } else {
                timetableDAO.update(selected.getId(), dept, course, day, start, end, venue, note);
                showStatus("Timetable entry updated.", false);
            }
            loadRows();
            handleNew();
        } catch (SQLException e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleDelete() {
        if (selected == null) {
            showStatus("Select a timetable entry first.", true);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete selected timetable entry?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.YES) return;
            try {
                timetableDAO.delete(selected.getId());
                showStatus("Timetable entry deleted.", false);
                loadRows();
                handleNew();
            } catch (SQLException e) {
                showStatus("Error: " + e.getMessage(), true);
            }
        });
    }

    private boolean isTime(String value) {
        return value.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }

    private void showStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.setStyle(error ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }
}
