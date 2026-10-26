package com.sms.model;

public class Course {

    private int id;
    private String code;
    private String title;
    private int credits;
    private Integer facultyId;      // may be null
    private String facultyName;     // filled by joins, display only

    public Course(int id, String code, String title, int credits, Integer facultyId) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.facultyId = facultyId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public Integer getFacultyId() { return facultyId; }
    public void setFacultyId(Integer facultyId) { this.facultyId = facultyId; }

    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }

    @Override
    public String toString() {
        return String.format("[Course #%d] %s | %s | %d credits | Faculty: %s",
            id, code, title, credits, facultyName == null ? "Unassigned" : facultyName);
    }
}
