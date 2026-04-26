package controller;

import dao.AttendanceDAO;
import dao.CaCalculator;
import dao.CourseDAO;
import dao.CourseMaterialDAO;
import dao.MarksDAO;
import dao.StudentProfileDAO;
import dao.TimetableDAO;
import dao.UserDAO;
import model.AttendanceSummary;
import model.Course;
import model.CourseMaterial;
import model.GradeResult;
import model.Medical;
import model.Timetable;
import util.Session;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// MEMBER 4 - Student Dashboard
// OOP: Polymorphism, Abstraction, Exception Handling, DB Handling, GUI
public class StudentDashboardController {

    // ── Grades tab ──────────────────────────────────────────
    @FXML private TableView<GradeResult>          gradeTable;
    @FXML private TableColumn<GradeResult,String> colCourse;
    @FXML private TableColumn<GradeResult,Double> colCA;
    @FXML private TableColumn<GradeResult,String> colCAStatus;
    @FXML private TableColumn<GradeResult,Double> colFinal;
    @FXML private TableColumn<GradeResult,Double> colTotal;
    @FXML private TableColumn<GradeResult,String> colGrade;
    @FXML private TableColumn<GradeResult,Double> colGP;
    @FXML private Label sgpaLabel;
    @FXML private Label cgpaLabel;
    @FXML private Label gradeStatusLabel;

    // ── Attendance tab ──────────────────────────────────────
    @FXML private ComboBox<String> attCourseCombo;
    @FXML private ComboBox<String> attTypeCombo;

    @FXML private TableView<AttendanceSummary>            attTable;
    @FXML private TableColumn<AttendanceSummary,String>   colACourse;
    @FXML private TableColumn<AttendanceSummary,Integer>  colAPresent;
    @FXML private TableColumn<AttendanceSummary,Double>   colAPct;
    @FXML private TableColumn<AttendanceSummary,Double>   colAEff;
    @FXML private TableColumn<AttendanceSummary,String>   colAElig;
    @FXML private Label attStatusLabel;

    // ── Medical tab ─────────────────────────────────────────
    @FXML private DatePicker medFromPicker;
    @FXML private DatePicker medToPicker;
    @FXML private TextField  medReasonField;
    @FXML private Label      medStatusLabel;

    @FXML private TableView<Medical>          medTable;
    @FXML private TableColumn<Medical,String> colMedFrom;
    @FXML private TableColumn<Medical,String> colMedTo;
    @FXML private TableColumn<Medical,String> colMedReason;
    @FXML private TableColumn<Medical,String> colMedStatus;

    @FXML private Label welcomeLabel;

    // ── Combined eligibility label ───────────────────────────
    @FXML private Label eligibilityLabel;

    // ── Course details tab ──────────────────────────────────
    @FXML private Label courseStatusLabel;
    @FXML private TableView<Course>           courseTable;
    @FXML private TableColumn<Course,String>  colCourseCode;
    @FXML private TableColumn<Course,String>  colCourseName;
    @FXML private TableColumn<Course,Integer> colCourseCredits;
    @FXML private TableColumn<Course,String>  colCourseLecturer;

    // ── Timetable tab ───────────────────────────────────────
    @FXML private Label timetableStatusLabel;
    @FXML private TableView<Timetable>             timetableTable;
    @FXML private TableColumn<Timetable,String>    colTDay;
    @FXML private TableColumn<Timetable,String>    colTStart;
    @FXML private TableColumn<Timetable,String>    colTEnd;
    @FXML private TableColumn<Timetable,String>    colTCourse;
    @FXML private TableColumn<Timetable,String>    colTVenue;
    @FXML private TableColumn<Timetable,String>    colTNote;

    // ── Course materials tab ───────────────────────────────
    @FXML private Label materialStatusLabel;
    @FXML private TableView<CourseMaterial>               materialTable;
    @FXML private TableColumn<CourseMaterial,String>      colMCourse;
    @FXML private TableColumn<CourseMaterial,String>      colMTitle;
    @FXML private TableColumn<CourseMaterial,String>      colMType;
    @FXML private TableColumn<CourseMaterial,String>      colMLink;
    @FXML private TableColumn<CourseMaterial,String>      colMBy;
    @FXML private TableColumn<CourseMaterial,String>      colMAt;

    // ── Profile tab ─────────────────────────────────────────
    @FXML private Label     profileIndexLabel;
    @FXML private Label     profileNameLabel;
    @FXML private Label     profileDeptLabel;
    @FXML private TextField contactEmailField;
    @FXML private TextField contactPhoneField;
    @FXML private ImageView profileImageView;
    @FXML private Label     picturePathLabel;
    @FXML private Label     profileStatusLabel;

    private final MarksDAO      marksDAO = new MarksDAO();
    private final AttendanceDAO attDAO   = new AttendanceDAO();
    private final CourseDAO     courseDAO = new CourseDAO();
    private final TimetableDAO  timetableDAO = new TimetableDAO();
    private final UserDAO       userDAO = new UserDAO();
    private final StudentProfileDAO studentProfileDAO = new StudentProfileDAO();
    private final CourseMaterialDAO materialDAO = new CourseMaterialDAO();

    private List<String> courseCodes = new ArrayList<>();
    private String selectedPicturePath;

    // The logged-in student's index number
    private String myId;

    @FXML
    public void initialize() {
        myId = Session.get().getUsername();
        welcomeLabel.setText("Welcome, " + Session.get().getFullName()
                             + "  |  Index: " + myId);

        loadLookups();

        attTypeCombo.setItems(FXCollections.observableArrayList("THEORY","PRACTICAL","ALL"));
        attTypeCombo.setValue("ALL");

        // Grade table
        colCourse.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCA.setCellValueFactory(new PropertyValueFactory<>("caMarks"));
        colCAStatus.setCellValueFactory(new PropertyValueFactory<>("caStatus"));
        colFinal.setCellValueFactory(new PropertyValueFactory<>("finalMarks"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalMarks"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colGP.setCellValueFactory(new PropertyValueFactory<>("gradePoint"));

        // Attendance table
        colACourse.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colAPresent.setCellValueFactory(new PropertyValueFactory<>("presentSessions"));
        colAPct.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        colAEff.setCellValueFactory(new PropertyValueFactory<>("effectivePct"));
        colAElig.setCellValueFactory(new PropertyValueFactory<>("eligibility"));

        // Medical table
        colMedFrom.setCellValueFactory(new PropertyValueFactory<>("fromDate"));
        colMedTo.setCellValueFactory(new PropertyValueFactory<>("toDate"));
        colMedReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colMedStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Course details table
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCourseCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colCourseLecturer.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));

        // Timetable table
        colTDay.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        colTStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colTEnd.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colTCourse.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colTVenue.setCellValueFactory(new PropertyValueFactory<>("venue"));
        colTNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        // Course materials table
        colMCourse.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colMTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colMType.setCellValueFactory(new PropertyValueFactory<>("materialType"));
        colMLink.setCellValueFactory(new PropertyValueFactory<>("materialLink"));
        colMBy.setCellValueFactory(new PropertyValueFactory<>("uploadedByName"));
        colMAt.setCellValueFactory(new PropertyValueFactory<>("uploadedAt"));

        // Auto-load grades on open
        handleViewMyGrades();
        handleViewMyCourseDetails();
        handleViewMyTimetable();
        handleViewMyMaterials();
        handleViewMyMedicals();
        loadProfileTab();
    }

    private void loadLookups() {
        try {
            List<Course> courses = courseDAO.getAll();
            courseCodes = courses.stream()
                    .map(Course::getCourseCode)
                    .distinct()
                    .collect(Collectors.toList());

            attCourseCombo.setItems(FXCollections.observableArrayList(courseCodes));
        } catch (Exception e) {
            gradeStatusLabel.setText("Failed to load courses: " + e.getMessage());
            gradeStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // ── Load my grades & SGPA ──────────────────────────────
    @FXML
    public void handleViewMyGrades() {
        try {
            List<GradeResult> list = marksDAO.getGradeResultsByStudent(myId);
            gradeTable.setItems(FXCollections.observableArrayList(list));

            double sgpa = marksDAO.calcSGPA(myId);
            double cgpa = marksDAO.calcCGPA(myId);
            sgpaLabel.setText("Your SGPA: " + sgpa);
            cgpaLabel.setText("Your CGPA: " + cgpa);

            gradeStatusLabel.setText("Grades loaded.");
            gradeStatusLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            gradeStatusLabel.setText("Error: " + e.getMessage());
            gradeStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // ── Load my attendance ─────────────────────────────────
    @FXML
    public void handleViewMyAttendance() {
        try {
            String course = attCourseCombo.getValue();
            String type   = attTypeCombo.getValue();
            if (course == null) {
                attStatusLabel.setText("Select a course.");
                attStatusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            AttendanceSummary s = attDAO.getSummary(myId, course, type);
            attTable.setItems(FXCollections.observableArrayList(s));
            attStatusLabel.setText("Attendance loaded.");
            attStatusLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            attStatusLabel.setText("Error: " + e.getMessage());
            attStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // ── Submit medical ─────────────────────────────────────
    @FXML
    public void handleSubmitMedical() {
        try {
            LocalDate from   = medFromPicker.getValue();
            LocalDate to     = medToPicker.getValue();
            String    reason = medReasonField.getText().trim();
            if (from==null||to==null||reason.isEmpty()) {
                medStatusLabel.setText("Fill all fields.");
                medStatusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            if (to.isBefore(from)) {
                medStatusLabel.setText("To must be after From.");
                medStatusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            attDAO.saveMedical(myId, from, to, reason);
            medStatusLabel.setText("Medical submitted! Pending approval.");
            medStatusLabel.setStyle("-fx-text-fill: green;");
            handleViewMyMedicals();
        } catch (Exception e) {
            medStatusLabel.setText("Error: " + e.getMessage());
            medStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // ── View my course details ─────────────────────────────
    @FXML
    public void handleViewMyCourseDetails() {
        try {
            courseTable.setItems(FXCollections.observableArrayList(courseDAO.getAll()));
            courseStatusLabel.setText("Course details loaded.");
            courseStatusLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            courseStatusLabel.setText("Error: " + e.getMessage());
            courseStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // ── View my timetable (department) ─────────────────────
    @FXML
    public void handleViewMyTimetable() {
        try {
            String dept = Session.get().getDept();
            if (dept == null || dept.isBlank()) {
                timetableStatusLabel.setText("Your department is not set.");
                timetableStatusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            timetableTable.setItems(FXCollections.observableArrayList(
                    timetableDAO.getByDept(dept)
            ));
            timetableStatusLabel.setText("Timetable loaded for " + dept + ".");
            timetableStatusLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            timetableStatusLabel.setText("Error: " + e.getMessage());
            timetableStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // ── View uploaded course materials ────────────────────
    @FXML
    public void handleViewMyMaterials() {
        try {
            List<CourseMaterial> list = materialDAO.getByCourseCodes(courseCodes);
            materialTable.setItems(FXCollections.observableArrayList(list));
            materialStatusLabel.setText(list.isEmpty()
                    ? "No materials uploaded yet."
                    : "Materials loaded.");
            materialStatusLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            materialStatusLabel.setText("Error: " + e.getMessage());
            materialStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // ── View my medicals ───────────────────────────────────
    @FXML
    public void handleViewMyMedicals() {
        try {
            medTable.setItems(FXCollections.observableArrayList(
                    attDAO.getMedicalsByStudent(myId)));
        } catch (Exception e) {
            medStatusLabel.setText("Error: " + e.getMessage());
            medStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // ── MEMBER 4: Combined eligibility check ──────────────
    // Checks BOTH attendance eligibility AND CA eligibility for every subject
    @FXML
    public void handleCheckEligibility() {
        try {
            StringBuilder sb = new StringBuilder();
            boolean allEligible = true;

            List<GradeResult> grades = marksDAO.getGradeResultsByStudent(myId);
            Map<String, Double> caByCourse = new HashMap<>();
            for (GradeResult g : grades) {
                caByCourse.put(g.getCourseCode(), g.getCaMarks());
            }

            for (String course : courseCodes) {
                // Attendance eligibility
                AttendanceSummary att = attDAO.getSummary(myId, course, "ALL");
                boolean attOk = att.getEffectivePct() >= 80;

                // CA eligibility
                boolean caOk = CaCalculator.isCaEligible(caByCourse.getOrDefault(course, 0.0));

                boolean eligible = attOk && caOk;
                if (!eligible) allEligible = false;

                sb.append(course)
                  .append(":  Attendance=").append(attOk ? "✓" : "✗")
                  .append("  CA=").append(caOk ? "✓" : "✗")
                  .append("  → ").append(eligible ? "ELIGIBLE" : "NOT ELIGIBLE")
                  .append("\n");
            }

            eligibilityLabel.setText(sb.toString());
            eligibilityLabel.setStyle(allEligible
                    ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        } catch (Exception e) {
            eligibilityLabel.setText("Error: " + e.getMessage());
            eligibilityLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // ── Load profile tab ───────────────────────────────────
    private void loadProfileTab() {
        profileIndexLabel.setText(Session.get().getUsername());
        profileNameLabel.setText(Session.get().getFullName());
        profileDeptLabel.setText(Session.get().getDept());
        contactEmailField.setText(Session.get().getEmail());
        contactPhoneField.setText(Session.get().getPhone());

        try {
            selectedPicturePath = studentProfileDAO.getProfilePicture(Session.get().getId());
            loadProfileImage(selectedPicturePath);
        } catch (Exception e) {
            profileStatusLabel.setText("Could not load profile picture: " + e.getMessage());
            profileStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void handleChooseProfilePicture() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Profile Picture");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp")
        );

        File selected = chooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (selected == null) return;

        selectedPicturePath = selected.getAbsolutePath();
        loadProfileImage(selectedPicturePath);
    }

    @FXML
    public void handleUpdateMyProfile() {
        String email = contactEmailField.getText().trim();
        String phone = contactPhoneField.getText().trim();

        if (email.isEmpty()) {
            profileStatusLabel.setText("Email is required.");
            profileStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            boolean updated = userDAO.updateStudentContact(Session.get().getId(), email, phone);
            if (!updated) {
                profileStatusLabel.setText("Profile update is not allowed.");
                profileStatusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if (selectedPicturePath != null && !selectedPicturePath.isBlank()) {
                studentProfileDAO.saveProfilePicture(Session.get().getId(), selectedPicturePath);
            }

            Session.get().setEmail(email);
            Session.get().setPhone(phone);
            profileStatusLabel.setText("Contact details/profile picture updated.");
            profileStatusLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            profileStatusLabel.setText("Error: " + e.getMessage());
            profileStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void loadProfileImage(String path) {
        if (path == null || path.isBlank()) {
            picturePathLabel.setText("No profile picture selected");
            profileImageView.setImage(null);
            return;
        }

        File imgFile = new File(path);
        if (!imgFile.exists()) {
            picturePathLabel.setText("Image not found: " + path);
            profileImageView.setImage(null);
            return;
        }

        picturePathLabel.setText(path);
        profileImageView.setImage(new Image(imgFile.toURI().toString(), true));
    }

    @FXML
    public void handleLogout() {
        try {
            Session.clear();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(
                new FXMLLoader(getClass().getResource("/view/Login.fxml")).load(), 480, 360));
            stage.setMaximized(false);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
