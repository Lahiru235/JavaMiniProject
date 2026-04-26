package model;

// -------------------------------------------------------
// CAGradeCalculator – checks if CA marks pass the threshold
// OOP: INHERITANCE (extends GradeCalculator)
//      POLYMORPHISM (overrides calculate())
// -------------------------------------------------------
public class CAGradeCalculator extends GradeCalculator {

    public CAGradeCalculator(String courseCode, double caMarks) {
        super(courseCode, caMarks);
    }

    @Override
    public String calculate() {
        // CA rule: must be >= 16/40 to pass (40%)
        return marks >= 16 ? "CA PASS" : "CA FAIL";
    }
}
