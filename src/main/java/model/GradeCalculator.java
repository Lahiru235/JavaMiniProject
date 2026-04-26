package model;

// -------------------------------------------------------
// GradeCalculator – Abstract class for grading
// OOP: ABSTRACTION (abstract class with abstract method)
//      Every type of calculator must implement calculate()
// -------------------------------------------------------
public abstract class GradeCalculator {

    protected String courseCode;
    protected double marks;

    public GradeCalculator(String courseCode, double marks) {
        this.courseCode = courseCode;
        this.marks      = marks;
    }

    // Abstract method – subclasses MUST implement this
    public abstract String calculate();

    // Shared method all subclasses can use
    public double getMarks() { return marks; }
}
