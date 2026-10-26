package com.sms.dao;

import com.sms.model.Exam;
import com.sms.model.ExamResult;
import com.sms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamDao implements CrudDao<Exam> {

    private static final String SELECT =
        "SELECT e.exam_id, e.course_id, e.exam_name, e.exam_date, e.max_marks, c.course_code "
      + "FROM exam e JOIN course c ON c.course_id = e.course_id";

    @Override
    public int add(Exam e) throws SQLException {
        String sql = "INSERT INTO exam (course_id, exam_name, exam_date, max_marks) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getCourseId());
            ps.setString(2, e.getName());
            ps.setDate(3, Date.valueOf(e.getDate()));
            ps.setInt(4, e.getMaxMarks());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    @Override
    public boolean update(Exam e) throws SQLException {
        String sql = "UPDATE exam SET course_id=?, exam_name=?, exam_date=?, max_marks=? WHERE exam_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, e.getCourseId());
            ps.setString(2, e.getName());
            ps.setDate(3, Date.valueOf(e.getDate()));
            ps.setInt(4, e.getMaxMarks());
            ps.setInt(5, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM exam WHERE exam_id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Exam> findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE e.exam_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Exam> findAll() throws SQLException {
        List<Exam> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(SELECT + " ORDER BY e.exam_date, e.exam_id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ---- results ----

    /** Inserts or updates a student's marks for an exam. */
    public void saveResult(int examId, int studentId, double marks) throws SQLException {
        String sql = "INSERT INTO exam_result (exam_id, student_id, marks_obtained) VALUES (?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE marks_obtained = VALUES(marks_obtained)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setInt(2, studentId);
            ps.setDouble(3, marks);
            ps.executeUpdate();
        }
    }

    public List<ExamResult> findResultsByExam(int examId) throws SQLException {
        String sql = RESULT_SELECT + " WHERE r.exam_id=? ORDER BY r.marks_obtained DESC";
        return queryResults(sql, examId);
    }

    public List<ExamResult> findResultsByStudent(int studentId) throws SQLException {
        String sql = RESULT_SELECT + " WHERE r.student_id=? ORDER BY e.exam_date";
        return queryResults(sql, studentId);
    }

    private static final String RESULT_SELECT =
        "SELECT r.exam_id, r.student_id, r.marks_obtained, s.name AS student_name, "
      + "e.exam_name, e.max_marks FROM exam_result r "
      + "JOIN student s ON s.student_id = r.student_id "
      + "JOIN exam e ON e.exam_id = r.exam_id";

    private List<ExamResult> queryResults(String sql, int param) throws SQLException {
        List<ExamResult> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExamResult r = new ExamResult(rs.getInt("exam_id"), rs.getInt("student_id"),
                        rs.getDouble("marks_obtained"));
                    r.setStudentName(rs.getString("student_name"));
                    r.setExamName(rs.getString("exam_name"));
                    r.setMaxMarks(rs.getInt("max_marks"));
                    list.add(r);
                }
            }
        }
        return list;
    }

    private Exam map(ResultSet rs) throws SQLException {
        Exam e = new Exam(rs.getInt("exam_id"), rs.getInt("course_id"), rs.getString("exam_name"),
            rs.getDate("exam_date").toLocalDate(), rs.getInt("max_marks"));
        e.setCourseCode(rs.getString("course_code"));
        return e;
    }
}
