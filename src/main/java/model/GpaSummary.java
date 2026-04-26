package model;

public class GpaSummary {

    private String studentId;
    private String studentName;
    private double sgpa;
    private double cgpa;

    public GpaSummary(String studentId, String studentName,
                      double sgpa, double cgpa) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.sgpa = sgpa;
        this.cgpa = cgpa;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public double getSgpa() { return sgpa; }
    public double getCgpa() { return cgpa; }
}
