package model;

public class Admin extends User {

    public Admin(int id, String username, String password,
                 String fullName, String email, String phone) {
        super(id, username, password, fullName, email, phone, "ADMIN", null);
    }

    @Override
    public String getDashboardTitle() { return "Admin Dashboard"; }
}
