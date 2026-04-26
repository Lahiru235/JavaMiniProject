package model;

public class CourseMaterial {

    private int id;
    private String courseCode;
    private String title;
    private String materialType;
    private String materialLink;
    private String description;
    private int uploadedBy;
    private String uploadedByName;
    private String uploadedAt;

    public CourseMaterial(int id, String courseCode, String title,
                          String materialType, String materialLink,
                          String description, int uploadedBy,
                          String uploadedByName, String uploadedAt) {
        this.id = id;
        this.courseCode = courseCode;
        this.title = title;
        this.materialType = materialType;
        this.materialLink = materialLink;
        this.description = description;
        this.uploadedBy = uploadedBy;
        this.uploadedByName = uploadedByName;
        this.uploadedAt = uploadedAt;
    }

    public int getId() { return id; }
    public String getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public String getMaterialType() { return materialType; }
    public String getMaterialLink() { return materialLink; }
    public String getDescription() { return description; }
    public int getUploadedBy() { return uploadedBy; }
    public String getUploadedByName() { return uploadedByName; }
    public String getUploadedAt() { return uploadedAt; }

    public void setCourseCode(String v) { courseCode = v; }
    public void setTitle(String v) { title = v; }
    public void setMaterialType(String v) { materialType = v; }
    public void setMaterialLink(String v) { materialLink = v; }
    public void setDescription(String v) { description = v; }
}
