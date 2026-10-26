package com.sms.model;

import java.time.LocalDate;

public class Exam {

    private int id;
    private int courseId;
    private String name;
    private LocalDate date;
    private int maxMarks;
    private String courseCode;      // filled by joins, display only

    public Exam(int id, int courseId, String name, LocalDate date, int maxMarks) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.date = date;
        this.maxMarks = maxMarks;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    @Override
    public String toString() {
        return String.format("[Exam #%d] %s | Course: %s | %s | Max marks: %d",
            id, name, courseCode == null ? String.valueOf(courseId) : courseCode, date, maxMarks);
    }
}
