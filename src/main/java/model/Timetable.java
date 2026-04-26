package model;

public class Timetable {

    private int id;
    private String dept;
    private String courseCode;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String venue;
    private String note;

    public Timetable(int id, String dept, String courseCode,
                     String dayOfWeek, String startTime, String endTime,
                     String venue, String note) {
        this.id = id;
        this.dept = dept;
        this.courseCode = courseCode;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
        this.note = note;
    }

    public int getId() { return id; }
    public String getDept() { return dept; }
    public String getCourseCode() { return courseCode; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getVenue() { return venue; }
    public String getNote() { return note; }

    public void setDept(String v) { dept = v; }
    public void setCourseCode(String v) { courseCode = v; }
    public void setDayOfWeek(String v) { dayOfWeek = v; }
    public void setStartTime(String v) { startTime = v; }
    public void setEndTime(String v) { endTime = v; }
    public void setVenue(String v) { venue = v; }
    public void setNote(String v) { note = v; }
}
