package com.sms.dao;

import com.sms.model.Student;
import com.sms.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDao implements CrudDao<Student> {

    private static final String SELECT = "SELECT student_id, name, email, phone, dob, department FROM student";

    @Override
    public int add(Student s) throws SQLException {
        String sql = "INSERT INTO student (name, email, phone, dob, department) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.setDate(4, s.getDob() == null ? null : Date.valueOf(s.getDob()));
            ps.setString(5, s.getDepartment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    @Override
    public boolean update(Student s) throws SQLException {
        String sql = "UPDATE student SET name=?, email=?, phone=?, dob=?, department=? WHERE student_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.setDate(4, s.getDob() == null ? null : Date.valueOf(s.getDob()));
            ps.setString(5, s.getDepartment());
            ps.setInt(6, s.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM student WHERE student_id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Student> findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE student_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(SELECT + " ORDER BY student_id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** Search by part of the name. */
    public List<Student> searchByName(String keyword) throws SQLException {
        List<Student> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE name LIKE ? ORDER BY name")) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Student map(ResultSet rs) throws SQLException {
        Date dob = rs.getDate("dob");
        LocalDate d = dob == null ? null : dob.toLocalDate();
        return new Student(rs.getInt("student_id"), rs.getString("name"), rs.getString("email"),
            rs.getString("phone"), d, rs.getString("department"));
    }
}
