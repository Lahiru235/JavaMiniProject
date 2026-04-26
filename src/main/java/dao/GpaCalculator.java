package dao;

import model.GradeResult;
import java.util.List;

// -------------------------------------------------------
// GpaCalculator – Handles SGPA and CGPA calculations
// -------------------------------------------------------
public class GpaCalculator {

    // ── Calculate SGPA from grade results ─────────────────
    public static double calculateSGPA(List<GradeResult> gradeResults) {
        double totalPoints = 0;
        int totalCredits = 0;
        
        for (GradeResult result : gradeResults) {
            totalPoints += result.getGradePoint() * result.getCredits();
            totalCredits += result.getCredits();
        }
        
        return totalCredits == 0 ? 0 : Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    // ── Calculate CGPA from grade results ─────────────────
    public static double calculateCGPA(List<GradeResult> gradeResults) {
        // With current project data model, CGPA is computed across all
        // available marks records for the student (same as SGPA)
        return calculateSGPA(gradeResults);
    }

    // ── Calculate weighted grade points ───────────────────
    public static double calculateWeightedGradePoints(List<GradeResult> gradeResults) {
        double totalPoints = 0;
        for (GradeResult result : gradeResults) {
            totalPoints += result.getGradePoint() * result.getCredits();
        }
        return totalPoints;
    }

    // ── Calculate total credits ───────────────────────────
    public static int calculateTotalCredits(List<GradeResult> gradeResults) {
        int totalCredits = 0;
        for (GradeResult result : gradeResults) {
            totalCredits += result.getCredits();
        }
        return totalCredits;
    }
}