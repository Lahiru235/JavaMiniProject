package model;

public class Notice {

    private int    id;
    private String title;
    private String content;
    private String createdBy;
    private String createdAt;

    public Notice(int id, String title, String content,
                  String createdBy, String createdAt) {
        this.id        = id;
        this.title     = title;
        this.content   = content;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int    getId()        { return id; }
    public String getTitle()     { return title; }
    public String getContent()   { return content; }
    public String getCreatedBy() { return createdBy; }
    public String getCreatedAt() { return createdAt; }

    public void setTitle(String v)   { title = v; }
    public void setContent(String v) { content = v; }
}
