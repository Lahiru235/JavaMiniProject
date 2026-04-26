package model;

public class Lecturer extends User {

    public Lecturer(int id, String username, String password,
                    String fullName, String email, String phone, String dept) {
        super(id, username, password, fullName, email, phone, "LECTURER", dept);
    }

    @Override
    public String getDashboardTitle() { return "Lecturer Dashboard"; }
}
