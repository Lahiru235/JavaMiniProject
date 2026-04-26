package controller;

import dao.AttendanceDAO;
import dao.CourseDAO;
import dao.TimetableDAO;
import dao.UserDAO;
import model.AttendanceSummary;
import model.Course;
import model.Medical;
import model.Timetable;
import model.User;
import util.Session;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// MEMBER 2 - Technical Officer Dashboard
// OOP concepts: Inheritance (Session.get() returns User subclass),
//               Polymorphism, Exception Handling, Database Handling, GUI
public class TechDashboardController {

    @FXML private ComboBox<String> courseCombo;
    @FXML private ComboBox<String> studentCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> sessionCombo;
    @FXML private DatePicker       sessionDatePicker;
    @FXML private CheckBox         presentCheck;
    @FXML private Label            attStatusLabel;

    @FXML private TableView<AttendanceSummary>          summaryTable;
    @FXML private TableColumn<AttendanceSummary,String> colStudent;
    @FXML private TableColumn<AttendanceSummary,String> colCourse;
    @FXML private TableColumn<AttendanceSummary,Integer> colPresent;
    @FXML private TableColumn<AttendanceSummary,Double> colPct;
    @FXML private TableColumn<AttendanceSummary,Double> colEffPct;
    @FXML private TableColumn<AttendanceSummary,String> colEligible;

    @FXML private TextField   medStudentField;
    @FXML private DatePicker  medFromPicker;
    @FXML private DatePicker  medToPicker;
    @FXML private TextField   medReasonField;
    @FXML private Label       medStatusLabel;

    @FXML private TableView<Medical>          medTable;
    @FXML private TableColumn<Medical,String> colMedStudent;
    @FXML private TableColumn<Medical,String> colMedFrom;
    @FXML private TableColumn<Medical,String> colMedTo;
    @FXML private TableColumn<Medical,String> colMedReason;
    @FXML private TableColumn<Medical,String> colMedStatus;

    @FXML private Label welcomeLabel;

    // ── Timetable view ──────────────────────────────────────
    @FXML private Label timetableStatusLabel;
    @FXML private TableView<Timetable>           timetableTable;
    @FXML private TableColumn<Timetable,String>  colTDay;
    @FXML private TableColumn<Timetable,String>  colTStart;
    @FXML private TableColumn<Timetable,String>  colTEnd;
    @FXML private TableColumn<Timetable,String>  colTCourse;
    @FXML private TableColumn<Timetable,String>  colTVenue;
    @FXML private TableColumn<Timetable,String>  colTNote;

    // ── Profile update ──────────────────────────────────────
    @FXML private TextField profNameField;
    @FXML private TextField profEmailField;
    @FXML private TextField profPhoneField;
    @FXML private TextField profDeptField;
    @FXML private Label     profileStatusLabel;

    private final AttendanceDAO attDAO = new AttendanceDAO();
    private final CourseDAO     courseDAO = new CourseDAO();
    private final UserDAO       userDAO = new UserDAO();
    private final TimetableDAO  timetableDAO = new TimetableDAO();

    private List<String> courseCodes = new ArrayList<>();
    private List<String> studentIds = new ArrayList<>();

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, " + Session.get().getFullName());
        loadLookups();
        typeCombo.setItems(FXCollections.observableArrayList("THEORY","PRACTICAL","ALL"));
        typeCombo.setValue("THEORY");
        String[] sessions = new String[15];
        for (int i = 0; i < 15; i++) sessions[i] = String.valueOf(i+1);
        sessionCombo.setItems(FXCollections.observableArrayList(sessions));
        sessionDatePicker.setValue(LocalDate.now());

        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colCourse.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colPresent.setCellValueFactory(new PropertyValueFactory<>("presentSessions"));
        colPct.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        colEffPct.setCellValueFactory(new PropertyValueFactory<>("effectivePct"));
        colEligible.setCellValueFactory(new PropertyValueFactory<>("eligibility"));

        colMedStudent.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colMedFrom.setCellValueFactory(new PropertyValueFactory<>("fromDate"));
        colMedTo.setCellValueFactory(new PropertyValueFactory<>("toDate"));
        colMedReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colMedStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Timetable table
        colTDay.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        colTStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colTEnd.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colTCourse.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colTVenue.setCellValueFactory(new PropertyValueFactory<>("venue"));
        colTNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        loadProfile();
        handleViewDeptTimetable();
    }

    private void loadLookups() {
        try {
            List<Course> courses = courseDAO.getAll();
            courseCodes = courses.stream()
                    .map(Course::getCourseCode)
                    .distinct()
                    .collect(Collectors.toList());
            courseCombo.setItems(FXCollections.observableArrayList(courseCodes));

            List<User> students = userDAO.getByRole("STUDENT");
            studentIds = students.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());
            studentCombo.setItems(FXCollections.observableArrayList(studentIds));
        } catch (Exception e) {
            setStatus(attStatusLabel, "Failed to load students/courses: " + e.getMessage(), false);
        }
    }

    @FXML
    public void handleSaveAttendance() {
        try {
            String s = studentCombo.getValue(), c = courseCombo.getValue(),
                   t = typeCombo.getValue(), sn = sessionCombo.getValue();
            LocalDate date = sessionDatePicker.getValue();
            if (s==null||c==null||sn==null||date==null) {
                setStatus(attStatusLabel,"Please fill all fields.",false); return;
            }
            if (t.equals("ALL")) {
                setStatus(attStatusLabel,"Choose THEORY or PRACTICAL to save.",false); return;
            }
            attDAO.saveAttendance(s, c, Integer.parseInt(sn), t, presentCheck.isSelected(), date);
            setStatus(attStatusLabel,"Attendance saved!",true);
        } catch (Exception e) {
            setStatus(attStatusLabel,"Error: "+e.getMessage(),false);
        }
    }

    @FXML
    public void handleViewBatchSummary() {
        try {
            String c = courseCombo.getValue(), t = typeCombo.getValue();
            if (c==null){setStatus(attStatusLabel,"Select a course.",false);return;}
            List<AttendanceSummary> list = attDAO.getBatchSummary(c, t);
            summaryTable.setItems(FXCollections.observableArrayList(list));
            setStatus(attStatusLabel,"Loaded "+list.size()+" records.",true);
        } catch (Exception e) { setStatus(attStatusLabel,"Error: "+e.getMessage(),false); }
    }

    @FXML
    public void handleViewIndividualSummary() {
        try {
            String s = studentCombo.getValue(), c = courseCombo.getValue(), t = typeCombo.getValue();
            if (s==null||c==null){setStatus(attStatusLabel,"Select student and course.",false);return;}
            AttendanceSummary sum = attDAO.getSummary(s, c, t);
            summaryTable.setItems(FXCollections.observableArrayList(sum));
            setStatus(attStatusLabel,"Loaded individual summary.",true);
        } catch (Exception e) { setStatus(attStatusLabel,"Error: "+e.getMessage(),false); }
    }

    @FXML
    public void handleSaveMedical() {
        try {
            String sid=medStudentField.getText().trim(), reason=medReasonField.getText().trim();
            LocalDate from=medFromPicker.getValue(), to=medToPicker.getValue();
            if (sid.isEmpty()||from==null||to==null||reason.isEmpty()) {
                setStatus(medStatusLabel,"Fill all medical fields.",false); return;
            }
            if (to.isBefore(from)){setStatus(medStatusLabel,"To must be after From.",false);return;}
            attDAO.saveMedical(sid, from, to, reason);
            setStatus(medStatusLabel,"Medical saved!",true);
            handleViewMedicals();
        } catch (Exception e) { setStatus(medStatusLabel,"Error: "+e.getMessage(),false); }
    }

    @FXML
    public void handleApproveMedical() {
        Medical sel = medTable.getSelectionModel().getSelectedItem();
        if (sel==null){setStatus(medStatusLabel,"Select a record first.",false);return;}
        try {
            attDAO.approveMedical(sel.getId(), true);
            setStatus(medStatusLabel,"Approved!",true);
            handleViewMedicals();
        } catch (Exception e) { setStatus(medStatusLabel,"Error: "+e.getMessage(),false); }
    }

    @FXML
    public void handleViewMedicals() {
        try {
            medTable.setItems(FXCollections.observableArrayList(attDAO.getAllMedicals()));
        } catch (Exception e) { setStatus(medStatusLabel,"Error: "+e.getMessage(),false); }
    }

    // ── Department timetable ───────────────────────────────
    @FXML
    public void handleViewDeptTimetable() {
        try {
            String dept = Session.get().getDept();
            if (dept == null || dept.isBlank()) {
                setStatus(timetableStatusLabel, "Department is not set for this user.", false);
                return;
            }
            timetableTable.setItems(FXCollections.observableArrayList(
                    timetableDAO.getByDept(dept)
            ));
            setStatus(timetableStatusLabel, "Timetable loaded for " + dept + ".", true);
        } catch (Exception e) {
            setStatus(timetableStatusLabel, "Error: " + e.getMessage(), false);
        }
    }

    // ── Profile update ─────────────────────────────────────
    private void loadProfile() {
        profNameField.setText(Session.get().getFullName());
        profEmailField.setText(Session.get().getEmail());
        profPhoneField.setText(Session.get().getPhone());
        profDeptField.setText(Session.get().getDept());
    }

    @FXML
    public void handleUpdateProfile() {
        String name = profNameField.getText().trim();
        String email = profEmailField.getText().trim();
        String phone = profPhoneField.getText().trim();
        String dept = profDeptField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            setStatus(profileStatusLabel, "Name and email are required.", false);
            return;
        }

        try {
            boolean ok = userDAO.updateStaffProfile(
                    Session.get().getId(), name, email, phone, dept
            );
            if (!ok) {
                setStatus(profileStatusLabel, "Profile update is not allowed.", false);
                return;
            }

            Session.get().setFullName(name);
            Session.get().setEmail(email);
            Session.get().setPhone(phone);
            Session.get().setDept(dept);
            welcomeLabel.setText("Welcome, " + Session.get().getFullName());
            setStatus(profileStatusLabel, "Profile updated.", true);
        } catch (Exception e) {
            setStatus(profileStatusLabel, "Error: " + e.getMessage(), false);
        }
    }

    private void setStatus(Label lbl, String msg, boolean ok) {
        lbl.setText(msg);
        lbl.setStyle(ok ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    @FXML
    public void handleLogout() {
        try {
            Session.clear();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/Login.fxml")).load(), 480, 360));
            stage.setMaximized(false);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
