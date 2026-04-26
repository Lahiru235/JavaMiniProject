package model;

public class TechnicalOfficer extends User {

    public TechnicalOfficer(int id, String username, String password,
                             String fullName, String email, String phone,
                             String dept) {
        super(id, username, password, fullName, email, phone, "TECH", dept);
    }

    @Override
    public String getDashboardTitle() { return "Technical Officer Dashboard"; }
}
