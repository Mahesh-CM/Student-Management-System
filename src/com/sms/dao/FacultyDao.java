package com.sms.dao;

import com.sms.model.Faculty;
import com.sms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FacultyDao implements CrudDao<Faculty> {

    private static final String SELECT = "SELECT faculty_id, name, email, phone, department FROM faculty";

    @Override
    public int add(Faculty f) throws SQLException {
        String sql = "INSERT INTO faculty (name, email, phone, department) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getName());
            ps.setString(2, f.getEmail());
            ps.setString(3, f.getPhone());
            ps.setString(4, f.getDepartment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    @Override
    public boolean update(Faculty f) throws SQLException {
        String sql = "UPDATE faculty SET name=?, email=?, phone=?, department=? WHERE faculty_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, f.getName());
            ps.setString(2, f.getEmail());
            ps.setString(3, f.getPhone());
            ps.setString(4, f.getDepartment());
            ps.setInt(5, f.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM faculty WHERE faculty_id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Faculty> findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE faculty_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Faculty> findAll() throws SQLException {
        List<Faculty> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(SELECT + " ORDER BY faculty_id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private Faculty map(ResultSet rs) throws SQLException {
        return new Faculty(rs.getInt("faculty_id"), rs.getString("name"), rs.getString("email"),
            rs.getString("phone"), rs.getString("department"));
    }
}
