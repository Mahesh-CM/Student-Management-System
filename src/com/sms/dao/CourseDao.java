package com.sms.dao;

import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDao implements CrudDao<Course> {

    private static final String SELECT =
        "SELECT c.course_id, c.course_code, c.title, c.credits, c.faculty_id, f.name AS faculty_name "
      + "FROM course c LEFT JOIN faculty f ON f.faculty_id = c.faculty_id";

    @Override
    public int add(Course course) throws SQLException {
        String sql = "INSERT INTO course (course_code, title, credits, faculty_id) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, course);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    @Override
    public boolean update(Course course) throws SQLException {
        String sql = "UPDATE course SET course_code=?, title=?, credits=?, faculty_id=? WHERE course_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, course);
            ps.setInt(5, course.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM course WHERE course_id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Course> findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE c.course_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Course> findAll() throws SQLException {
        List<Course> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(SELECT + " ORDER BY c.course_id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ---- enrollment ----

    public boolean enroll(int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO enrollment (student_id, course_id) VALUES (?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean unenroll(int studentId, int courseId) throws SQLException {
        String sql = "DELETE FROM enrollment WHERE student_id=? AND course_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Course> findByStudent(int studentId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = SELECT + " JOIN enrollment e ON e.course_id = c.course_id WHERE e.student_id=? ORDER BY c.course_code";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Student> findStudentsInCourse(int courseId) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.name, s.email, s.phone, s.dob, s.department "
                   + "FROM student s JOIN enrollment e ON e.student_id = s.student_id "
                   + "WHERE e.course_id=? ORDER BY s.name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date dob = rs.getDate("dob");
                    LocalDate d = dob == null ? null : dob.toLocalDate();
                    list.add(new Student(rs.getInt("student_id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("phone"), d, rs.getString("department")));
                }
            }
        }
        return list;
    }

    // ---- helpers ----

    private void bind(PreparedStatement ps, Course course) throws SQLException {
        ps.setString(1, course.getCode());
        ps.setString(2, course.getTitle());
        ps.setInt(3, course.getCredits());
        if (course.getFacultyId() == null) ps.setNull(4, Types.INTEGER);
        else ps.setInt(4, course.getFacultyId());
    }

    private Course map(ResultSet rs) throws SQLException {
        int fid = rs.getInt("faculty_id");
        Integer facultyId = rs.wasNull() ? null : fid;
        Course course = new Course(rs.getInt("course_id"), rs.getString("course_code"),
            rs.getString("title"), rs.getInt("credits"), facultyId);
        course.setFacultyName(rs.getString("faculty_name"));
        return course;
    }
}
