package model;

import java.time.LocalDate;

// -------------------------------------------------------
// Medical – one medical record submitted by a student
// OOP: Encapsulation
// -------------------------------------------------------
public class Medical {

    private int       id;
    private String    studentId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String    reason;
    private boolean   approved;   // true = approved by TO

    public Medical(int id, String studentId,
                   LocalDate fromDate, LocalDate toDate,
                   String reason, boolean approved) {
        this.id        = id;
        this.studentId = studentId;
        this.fromDate  = fromDate;
        this.toDate    = toDate;
        this.reason    = reason;
        this.approved  = approved;
    }

    public int       getId()        { return id; }
    public String    getStudentId() { return studentId; }
    public LocalDate getFromDate()  { return fromDate; }
    public LocalDate getToDate()    { return toDate; }
    public String    getReason()    { return reason; }
    public boolean   isApproved()   { return approved; }

    public void setApproved(boolean approved) { this.approved = approved; }
    public void setReason(String reason)      { this.reason = reason; }

    // Handy display string for the UI table
    public String getStatus() { return approved ? "Approved" : "Pending"; }
}
