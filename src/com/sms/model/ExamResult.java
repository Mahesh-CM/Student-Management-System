package com.sms.model;

public class ExamResult {

    private final int examId;
    private final int studentId;
    private final double marks;
    private String studentName;     // display only
    private String examName;        // display only
    private int maxMarks;           // display only

    public ExamResult(int examId, int studentId, double marks) {
        this.examId = examId;
        this.studentId = studentId;
        this.marks = marks;
    }

    public int getExamId() { return examId; }
    public int getStudentId() { return studentId; }
    public double getMarks() { return marks; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }

    @Override
    public String toString() {
        double pct = maxMarks > 0 ? marks * 100.0 / maxMarks : 0;
        return String.format("%s (#%d) | %s | %.2f/%d (%.1f%%)",
            studentName, studentId, examName, marks, maxMarks, pct);
    }
}
