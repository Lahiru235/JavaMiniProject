package dao;

// -------------------------------------------------------
// GradeCalculator – Handles all grade and grade point 
// calculations based on UGC Circular 12/2024
// -------------------------------------------------------
public class GradeCalculator {

    public static final double END_EXAM_MAX = 60.0;
    public static final double TOTAL_MAX = 100.0;

    private GradeCalculator() {
        // Utility class
    }

    // ── UGC Circular 12/2024 – Grade from total marks ─────
    public static String calculateGrade(double marks) {
        if (marks >= 85) return "A+";
        if (marks >= 75) return "A";
        if (marks >= 70) return "A-";
        if (marks >= 65) return "B+";
        if (marks >= 60) return "B";
        if (marks >= 55) return "B-";
        if (marks >= 50) return "C+";
        if (marks >= 45) return "C";
        if (marks >= 40) return "C-";
        if (marks >= 35) return "D";
        return "E";
    }

    // ── Calculate grade point from total marks ────────────
    public static double calculateGradePoint(double marks) {
        if (marks >= 85) return 4.0;
        if (marks >= 75) return 4.0;
        if (marks >= 70) return 3.7;
        if (marks >= 65) return 3.3;
        if (marks >= 60) return 3.0;
        if (marks >= 55) return 2.7;
        if (marks >= 50) return 2.3;
        if (marks >= 45) return 2.0;
        if (marks >= 40) return 1.7;
        if (marks >= 35) return 1.3;
        return 0.0;
    }

    public static double calculateTotalMarks(double caMarks, double endExamMarks) {
        validateStoredMarks(caMarks, endExamMarks);
        return caMarks + endExamMarks;
    }

    // ── Determine grade considering CA gate ───────────────
    public static String determineGrade(double caMarks, double endExamMarks) {
        if (!CaCalculator.isCaEligible(caMarks)) {
            return "E(CA)";
        }
        return calculateGrade(calculateTotalMarks(caMarks, endExamMarks));
    }

    // ── Determine grade point considering CA gate ─────────
    public static double determineGradePoint(double caMarks, double endExamMarks) {
        if (!CaCalculator.isCaEligible(caMarks)) {
            return 0.0;
        }
        return calculateGradePoint(calculateTotalMarks(caMarks, endExamMarks));
    }

    // ── Validate persisted marks range (CA + end exam) ────
    public static void validateStoredMarks(double caMarks, double endExamMarks) {
        validateRange("CA marks", caMarks, 0, CaCalculator.CA_MAX);
        validateRange("End exam marks", endExamMarks, 0, END_EXAM_MAX);
    }

    private static void validateRange(String label, double value, double min, double max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(label + " must be between " + min + " and " + max + ".");
        }
    }
}