package controller;

import dao.CourseDAO;
import dao.CourseMaterialDAO;
import dao.MarksDAO;
import dao.CaCalculator;
import dao.GradeCalculator;
import dao.AttendanceDAO;
import dao.UserDAO;
import model.Course;
import model.CourseMaterial;
import model.EligibilityResult;
import model.GpaSummary;
import model.GradeResult;
import model.AttendanceSummary;
import model.Medical;
import model.User;
import util.Session;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// MEMBER 3 - Lecturer Dashboard (Marks & Grading)
// OOP: Abstraction, Polymorphism, Exception Handling, DB Handling, GUI
public class LecturerDashboardController {

    // ── Marks entry ─────────────────────────────────────────
    @FXML private ComboBox<String> markStudentCombo;
    @FXML private ComboBox<String> markCourseCombo;
    @FXML private TextField        quiz1Field;
    @FXML private TextField        quiz2Field;
    @FXML private TextField        quiz3Field;
    @FXML private TextField        midField;
    @FXML private TextField        endExamField;
    @FXML private Label            marksStatusLabel;
    @FXML private Label            sgpaLabel;
    @FXML private Label            cgpaLabel;

    // ── Marks view ──────────────────────────────────────────
    @FXML private TableView<GradeResult>          gradeTable;
    @FXML private TableColumn<GradeResult,String> colGStudent;
    @FXML private TableColumn<GradeResult,String> colGName;
    @FXML private TableColumn<GradeResult,String> colGCourse;
    @FXML private TableColumn<GradeResult,Double> colGCA;
    @FXML private TableColumn<GradeResult,String> colGCAStatus;
    @FXML private TableColumn<GradeResult,Double> colGFinal;
    @FXML private TableColumn<GradeResult,Double> colGTotal;
    @FXML private TableColumn<GradeResult,String> colGGrade;
    @FXML private TableColumn<GradeResult,Double> colGGP;

    // ── GPA view ────────────────────────────────────────────
    @FXML private TableView<GpaSummary>           gpaTable;
    @FXML private TableColumn<GpaSummary,String>  colGSid;
    @FXML private TableColumn<GpaSummary,String>  colGName2;
    @FXML private TableColumn<GpaSummary,Double>  colGSgpa;
    @FXML private TableColumn<GpaSummary,Double>  colGCgpa;

    // ── Attendance view ─────────────────────────────────────
    @FXML private ComboBox<String> attStudentCombo;
    @FXML private ComboBox<String> attCourseCombo;
    @FXML private ComboBox<String> attTypeCombo;
    @FXML private Label            attLabel;

    @FXML private TableView<AttendanceSummary>            attTable;
    @FXML private TableColumn<AttendanceSummary,String>   colAStudent;
    @FXML private TableColumn<AttendanceSummary,String>   colACourse;
    @FXML private TableColumn<AttendanceSummary,Integer>  colAPresent;
    @FXML private TableColumn<AttendanceSummary,Double>   colAPct;
    @FXML private TableColumn<AttendanceSummary,Double>   colAEff;
    @FXML private TableColumn<AttendanceSummary,String>   colAElig;

    // ── Combined eligibility ────────────────────────────────
    @FXML private ComboBox<String> eligCourseCombo;
    @FXML private ComboBox<String> eligStudentCombo;
    @FXML private Label            eligStatusLabel;

    @FXML private TableView<EligibilityResult>            eligTable;
    @FXML private TableColumn<EligibilityResult,String>   colEStudent;
    @FXML private TableColumn<EligibilityResult,String>   colEName;
    @FXML private TableColumn<EligibilityResult,String>   colECourse;
    @FXML private TableColumn<EligibilityResult,Double>   colEAtt;
    @FXML private TableColumn<EligibilityResult,Double>   colECA;
    @FXML private TableColumn<EligibilityResult,String>   colEStatus;

    // ── Medical records ─────────────────────────────────────
    @FXML private TextField medStudentFilterField;
    @FXML private Label     medStatusLabel;

    @FXML private TableView<Medical>          medTable;
    @FXML private TableColumn<Medical,String> colMedStudent;
    @FXML private TableColumn<Medical,String> colMedFrom;
    @FXML private TableColumn<Medical,String> colMedTo;
    @FXML private TableColumn<Medical,String> colMedReason;
    @FXML private TableColumn<Medical,String> colMedStatus;

    // ── Course materials ────────────────────────────────────
    @FXML private ComboBox<String> materialCourseCombo;
    @FXML private ComboBox<String> materialTypeCombo;
    @FXML private TextField        materialTitleField;
    @FXML private TextField        materialLinkField;
    @FXML private TextArea         materialDescArea;
    @FXML private Label            materialStatusLabel;

    @FXML private TableView<CourseMaterial>               materialTable;
    @FXML private TableColumn<CourseMaterial,String>      colMCourse;
    @FXML private TableColumn<CourseMaterial,String>      colMTitle;
    @FXML private TableColumn<CourseMaterial,String>      colMType;
    @FXML private TableColumn<CourseMaterial,String>      colMLink;
    @FXML private TableColumn<CourseMaterial,String>      colMBy;
    @FXML private TableColumn<CourseMaterial,String>      colMAt;

    // ── Undergraduate details ───────────────────────────────
    @FXML private TableView<User>           studentTable;
    @FXML private TableColumn<User,String>  colUIndex;
    @FXML private TableColumn<User,String>  colUName;
    @FXML private TableColumn<User,String>  colUEmail;
    @FXML private TableColumn<User,String>  colUPhone;
    @FXML private TableColumn<User,String>  colUDept;

    // ── Profile update ──────────────────────────────────────
    @FXML private TextField profNameField;
    @FXML private TextField profEmailField;
    @FXML private TextField profPhoneField;
    @FXML private TextField profDeptField;
    @FXML private Label     profileStatusLabel;

    @FXML private Label welcomeLabel;

    private final MarksDAO      marksDAO = new MarksDAO();
    private final AttendanceDAO attDAO   = new AttendanceDAO();
    private final CourseDAO     courseDAO = new CourseDAO();
    private final UserDAO       userDAO = new UserDAO();
    private final CourseMaterialDAO materialDAO = new CourseMaterialDAO();

    private List<User> students = new ArrayList<>();
    private List<String> courseCodes = new ArrayList<>();
    private CourseMaterial selectedMaterial;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, " + Session.get().getFullName());
        loadLookups();

        // Grade table
        colGStudent.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colGName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colGCourse.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colGCA.setCellValueFactory(new PropertyValueFactory<>("caMarks"));
        colGCAStatus.setCellValueFactory(new PropertyValueFactory<>("caStatus"));
        colGFinal.setCellValueFactory(new PropertyValueFactory<>("finalMarks"));
        colGTotal.setCellValueFactory(new PropertyValueFactory<>("totalMarks"));
        colGGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colGGP.setCellValueFactory(new PropertyValueFactory<>("gradePoint"));

        // GPA table
        colGSid.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colGName2.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colGSgpa.setCellValueFactory(new PropertyValueFactory<>("sgpa"));
        colGCgpa.setCellValueFactory(new PropertyValueFactory<>("cgpa"));

        // Attendance table
        colAStudent.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colACourse.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colAPresent.setCellValueFactory(new PropertyValueFactory<>("presentSessions"));
        colAPct.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        colAEff.setCellValueFactory(new PropertyValueFactory<>("effectivePct"));
        colAElig.setCellValueFactory(new PropertyValueFactory<>("eligibility"));

        // Eligibility table
        colEStudent.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colEName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colECourse.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colEAtt.setCellValueFactory(new PropertyValueFactory<>("attendancePct"));
        colECA.setCellValueFactory(new PropertyValueFactory<>("caMarks"));
        colEStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Medical table
        colMedStudent.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colMedFrom.setCellValueFactory(new PropertyValueFactory<>("fromDate"));
        colMedTo.setCellValueFactory(new PropertyValueFactory<>("toDate"));
        colMedReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colMedStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Materials table
        colMCourse.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colMTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colMType.setCellValueFactory(new PropertyValueFactory<>("materialType"));
        colMLink.setCellValueFactory(new PropertyValueFactory<>("materialLink"));
        colMBy.setCellValueFactory(new PropertyValueFactory<>("uploadedByName"));
        colMAt.setCellValueFactory(new PropertyValueFactory<>("uploadedAt"));

        materialTypeCombo.setItems(FXCollections.observableArrayList(
                "SLIDE", "DOCUMENT", "LINK", "VIDEO", "OTHER"
        ));

        materialTable.getSelectionModel().selectedItemProperty().addListener((o, old, m) -> {
            if (m == null) return;
            selectedMaterial = m;
            materialCourseCombo.setValue(m.getCourseCode());
            materialTypeCombo.setValue(m.getMaterialType());
            materialTitleField.setText(m.getTitle());
            materialLinkField.setText(m.getMaterialLink() == null ? "" : m.getMaterialLink());
            materialDescArea.setText(m.getDescription() == null ? "" : m.getDescription());
        });

        // Undergraduate details table
        colUIndex.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colUDept.setCellValueFactory(new PropertyValueFactory<>("dept"));

        loadStudentDetails();
        loadMedicals();
        loadMaterials(false);
        loadProfile();
        handleViewBatchGrades();
    }

    private void loadLookups() {
        try {
            students = userDAO.getByRole("STUDENT");
            List<String> studentIds = students.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            List<Course> courses = courseDAO.getAll();
            courseCodes = courses.stream()
                    .map(Course::getCourseCode)
                    .distinct()
                    .collect(Collectors.toList());

            markStudentCombo.setItems(FXCollections.observableArrayList(studentIds));
            attStudentCombo.setItems(FXCollections.observableArrayList(studentIds));
            eligStudentCombo.setItems(FXCollections.observableArrayList(studentIds));

            markCourseCombo.setItems(FXCollections.observableArrayList(courseCodes));
            attCourseCombo.setItems(FXCollections.observableArrayList(courseCodes));
            eligCourseCombo.setItems(FXCollections.observableArrayList(courseCodes));
            materialCourseCombo.setItems(FXCollections.observableArrayList(courseCodes));

            attTypeCombo.setItems(FXCollections.observableArrayList("THEORY", "PRACTICAL", "ALL"));
            attTypeCombo.setValue("ALL");
        } catch (Exception e) {
            setStatus(marksStatusLabel, "Failed to load students/courses: " + e.getMessage(), false);
        }
    }

    // ── Save marks ──────────────────────────────────────────
    @FXML
    public void handleSaveMarks() {
        try {
            String sid    = markStudentCombo.getValue();
            String course = markCourseCombo.getValue();
            if (sid==null||course==null) {
                setStatus(marksStatusLabel,"Select student and course.",false); return;
            }
            double quiz1 = Double.parseDouble(quiz1Field.getText().trim());
            double quiz2 = Double.parseDouble(quiz2Field.getText().trim());
            double quiz3 = Double.parseDouble(quiz3Field.getText().trim());
            double mid = Double.parseDouble(midField.getText().trim());
            double end = Double.parseDouble(endExamField.getText().trim());

            double ca = CaCalculator.calculateCaMarks(quiz1, quiz2, quiz3, mid);
            double total = GradeCalculator.calculateTotalMarks(ca, end);

            marksDAO.saveMarks(sid, course, quiz1, quiz2, quiz3, mid, end);
            setStatus(marksStatusLabel,
                    "Marks saved! CA=" + ca + ", Total=" + total
                            + ", Grade=" + GradeCalculator.determineGrade(ca, end),
                    true);

        } catch (NumberFormatException e) {
            setStatus(marksStatusLabel,"Enter valid numbers for quiz/mid/end marks.",false);
        } catch (IllegalArgumentException e) {
            setStatus(marksStatusLabel,e.getMessage(),false);
        } catch (Exception e) {
            setStatus(marksStatusLabel,"Error: "+e.getMessage(),false);
        }
    }

    // ── View all batch grades ───────────────────────────────
    @FXML
    public void handleViewBatchGrades() {
        try {
            List<GradeResult> list = marksDAO.getAllGradeResults();
            gradeTable.setItems(FXCollections.observableArrayList(list));
            sgpaLabel.setText("");
            cgpaLabel.setText("");
        } catch (Exception e) {
            setStatus(marksStatusLabel,"Error: "+e.getMessage(),false);
        }
    }

    // ── View one student's grades + SGPA ───────────────────
    @FXML
    public void handleViewStudentGrades() {
        try {
            String sid = markStudentCombo.getValue();
            if (sid==null){setStatus(marksStatusLabel,"Select a student.",false);return;}
            List<GradeResult> list = marksDAO.getGradeResultsByStudent(sid);
            gradeTable.setItems(FXCollections.observableArrayList(list));
            double sgpa = marksDAO.calcSGPA(sid);
            double cgpa = marksDAO.calcCGPA(sid);
            sgpaLabel.setText("SGPA for " + sid + ": " + sgpa);
            cgpaLabel.setText("CGPA for " + sid + ": " + cgpa);
        } catch (Exception e) {
            setStatus(marksStatusLabel,"Error: "+e.getMessage(),false);
        }
    }

    @FXML
    public void handleViewBatchGpa() {
        try {
            gpaTable.setItems(FXCollections.observableArrayList(marksDAO.getBatchGpaSummary()));
        } catch (Exception e) {
            setStatus(marksStatusLabel, "Error: " + e.getMessage(), false);
        }
    }

    // ── View attendance (batch) ─────────────────────────────
    @FXML
    public void handleViewBatchAttendance() {
        try {
            String c = attCourseCombo.getValue(), t = attTypeCombo.getValue();
            if (c==null){attLabel.setText("Select a course.");return;}
            attTable.setItems(FXCollections.observableArrayList(attDAO.getBatchSummary(c,t)));
            attLabel.setText("Attendance loaded.");
        } catch (Exception e) { attLabel.setText("Error: "+e.getMessage()); }
    }

    // ── View attendance (individual) ────────────────────────
    @FXML
    public void handleViewStudentAttendance() {
        try {
            String s=attStudentCombo.getValue(), c=attCourseCombo.getValue(), t=attTypeCombo.getValue();
            if (s==null||c==null){attLabel.setText("Select student and course.");return;}
            attTable.setItems(FXCollections.observableArrayList(attDAO.getSummary(s,c,t)));
            attLabel.setText("Attendance loaded.");
        } catch (Exception e) { attLabel.setText("Error: "+e.getMessage()); }
    }

    // ── Combined eligibility (attendance + CA) ─────────────
    @FXML
    public void handleViewBatchEligibility() {
        try {
            String course = eligCourseCombo.getValue();
            if (course == null) {
                setStatus(eligStatusLabel, "Select a course.", false);
                return;
            }

            List<EligibilityResult> list = new ArrayList<>();
            for (User s : students) {
                list.add(buildEligibility(s.getUsername(), course));
            }
            eligTable.setItems(FXCollections.observableArrayList(list));
            setStatus(eligStatusLabel, "Loaded batch eligibility.", true);
        } catch (Exception e) {
            setStatus(eligStatusLabel, "Error: " + e.getMessage(), false);
        }
    }

    @FXML
    public void handleViewStudentEligibility() {
        try {
            String student = eligStudentCombo.getValue();
            String course = eligCourseCombo.getValue();
            if (student == null || course == null) {
                setStatus(eligStatusLabel, "Select student and course.", false);
                return;
            }
            EligibilityResult result = buildEligibility(student, course);
            eligTable.setItems(FXCollections.observableArrayList(result));
            setStatus(eligStatusLabel, "Loaded individual eligibility.", true);
        } catch (Exception e) {
            setStatus(eligStatusLabel, "Error: " + e.getMessage(), false);
        }
    }

    private EligibilityResult buildEligibility(String studentId, String course) throws Exception {
        AttendanceSummary a = attDAO.getSummary(studentId, course, "ALL");
        double ca = marksDAO.getCaMarks(studentId, course);
        boolean eligible = a.getEffectivePct() >= 80 && CaCalculator.isCaEligible(ca);
        return new EligibilityResult(studentId, a.getStudentName(), course,
                a.getEffectivePct(), ca, eligible);
    }

    // ── Medical records ─────────────────────────────────────
    @FXML
    public void handleViewMedicals() {
        loadMedicals();
    }

    private void loadMedicals() {
        try {
            List<Medical> list = attDAO.getAllMedicals();
            String filter = medStudentFilterField.getText() == null
                    ? "" : medStudentFilterField.getText().trim();
            if (!filter.isEmpty()) {
                list = list.stream()
                        .filter(m -> m.getStudentId().equalsIgnoreCase(filter))
                        .collect(Collectors.toList());
            }
            medTable.setItems(FXCollections.observableArrayList(list));
            setStatus(medStatusLabel, "Medical records loaded.", true);
        } catch (Exception e) {
            setStatus(medStatusLabel, "Error: " + e.getMessage(), false);
        }
    }

    // ── Course materials ────────────────────────────────────
    @FXML
    public void handleMaterialNew() {
        selectedMaterial = null;
        materialCourseCombo.getSelectionModel().clearSelection();
        materialTypeCombo.getSelectionModel().clearSelection();
        materialTitleField.clear();
        materialLinkField.clear();
        materialDescArea.clear();
        materialStatusLabel.setText("");
    }

    @FXML
    public void handleMaterialSave() {
        String course = materialCourseCombo.getValue();
        String type = materialTypeCombo.getValue();
        String title = materialTitleField.getText().trim();
        String link = materialLinkField.getText().trim();
        String desc = materialDescArea.getText().trim();

        if (course == null || type == null || title.isEmpty()) {
            setStatus(materialStatusLabel, "Course, type and title are required.", false);
            return;
        }

        try {
            int lecturerId = Session.get().getId();
            if (selectedMaterial == null) {
                materialDAO.add(course, title, type, link, desc, lecturerId);
                setStatus(materialStatusLabel, "Material added.", true);
            } else {
                boolean updated = materialDAO.updateForLecturer(
                        selectedMaterial.getId(), lecturerId,
                        course, title, type, link, desc
                );
                if (!updated) {
                    setStatus(materialStatusLabel, "You can only edit your own materials.", false);
                    return;
                }
                setStatus(materialStatusLabel, "Material updated.", true);
            }
            loadMaterials(false);
            handleMaterialNew();
        } catch (Exception e) {
            setStatus(materialStatusLabel, "Error: " + e.getMessage(), false);
        }
    }

    @FXML
    public void handleMaterialDelete() {
        if (selectedMaterial == null) {
            setStatus(materialStatusLabel, "Select a material first.", false);
            return;
        }

        try {
            boolean deleted = materialDAO.deleteForLecturer(
                    selectedMaterial.getId(), Session.get().getId()
            );
            if (!deleted) {
                setStatus(materialStatusLabel, "You can only delete your own materials.", false);
                return;
            }
            setStatus(materialStatusLabel, "Material deleted.", true);
            loadMaterials(false);
            handleMaterialNew();
        } catch (Exception e) {
            setStatus(materialStatusLabel, "Error: " + e.getMessage(), false);
        }
    }

    @FXML
    public void handleLoadAllMaterials() {
        loadMaterials(false);
    }

    @FXML
    public void handleLoadMyMaterials() {
        loadMaterials(true);
    }

    private void loadMaterials(boolean onlyMine) {
        try {
            List<CourseMaterial> list = onlyMine
                    ? materialDAO.getByLecturer(Session.get().getId())
                    : materialDAO.getAll();
            materialTable.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            setStatus(materialStatusLabel, "Error: " + e.getMessage(), false);
        }
    }

    // ── Undergraduate details ───────────────────────────────
    @FXML
    public void handleRefreshUndergraduates() {
        loadStudentDetails();
    }

    private void loadStudentDetails() {
        try {
            studentTable.setItems(FXCollections.observableArrayList(students));
        } catch (Exception e) {
            // Keep this silent from blocking other tabs.
        }
    }

    // ── Profile update ──────────────────────────────────────
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
