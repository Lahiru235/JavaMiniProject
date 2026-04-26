package model;

public abstract class User {

    private int    id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String dept;

    public User(int id, String username, String password,
                String fullName, String email, String phone,
                String role, String dept) {
        this.id       = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email    = email;
        this.phone    = phone;
        this.role     = role;
        this.dept     = dept;
    }

    // Every subclass must provide a dashboard title
    public abstract String getDashboardTitle();

    // Getters
    public int    getId()       { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public String getRole()     { return role; }
    public String getDept()     { return dept; }

    // Setters
    public void setFullName(String v) { fullName = v; }
    public void setEmail(String v)    { email = v; }
    public void setPhone(String v)    { phone = v; }
    public void setDept(String v)     { dept = v; }
    public void setPassword(String v) { password = v; }
}
