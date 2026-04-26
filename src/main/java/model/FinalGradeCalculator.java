package model;

import dao.GradeCalculator;

// -------------------------------------------------------
// FinalGradeCalculator – calculates grade from TOTAL marks
// OOP: INHERITANCE (extends GradeCalculator)
//      POLYMORPHISM (overrides calculate())
// -------------------------------------------------------
public class FinalGradeCalculator extends model.GradeCalculator {

    public FinalGradeCalculator(String courseCode, double totalMarks) {
        super(courseCode, totalMarks);
    }

    @Override
    public String calculate() {
        return GradeCalculator.calculateGrade(marks);
    }
}
