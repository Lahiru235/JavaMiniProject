package model;

public class Student extends User {

    public Student(int id, String username, String password,
                   String fullName, String email, String phone, String dept) {
        super(id, username, password, fullName, email, phone, "STUDENT", dept);
    }

    @Override
    public String getDashboardTitle() { return "Student Portal"; }

    // username IS the student index number (TG1701, TG1702 etc.)
    public String getIndexNumber() { return getUsername(); }
}
