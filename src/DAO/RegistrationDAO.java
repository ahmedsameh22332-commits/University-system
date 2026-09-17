package DAO;

import DatabaseConnection.DBConnection;
import model.Registration;
import Status.Role;
import Status.RegistrationStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {


    public void insert(Registration r) throws SQLException {
        String sql = "insert into pending_registrations (name, address, role, password, status, degree, course_id) values (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, r.getName());
            stmt.setString(2, r.getAddress());
            stmt.setInt(3, r.getRole().toDbValue());
            stmt.setString(4, r.getPassword());
            stmt.setInt(5, r.getStatus().toDbValue());
            stmt.setString(6, r.getDegree());
            if (r.getCourseId() == null) stmt.setNull(7, Types.INTEGER);
            else stmt.setInt(7, r.getCourseId());
            stmt.executeUpdate();
        }
    }

    public List<Registration> findPending() throws SQLException {
        String sql = "select * from pending_registrations where status = 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Registration> list = new ArrayList<>();
            while (rs.next()) {
                list.add(buildRegistration(rs));
            }
            return list;
        }
    }

    public Registration findById(int requestId) throws SQLException {
        String sql = "select * from pending_registrations where request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return buildRegistration(rs);
            }
        }
    }


    public Registration findByNameAndPassword(String name, String password) throws SQLException {
        String sql = "select * from pending_registrations where name = ? and password = ? order by request_id desc limit 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return buildRegistration(rs);
            }
        }
    }

    public void reject(int requestId, String reason) throws SQLException {
        String sql = "update pending_registrations set status = ?, reason = ? where request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, RegistrationStatus.REJECTED.toDbValue());
            stmt.setString(2, reason);
            stmt.setInt(3, requestId);
            stmt.executeUpdate();
        }
    }

    public void approve(int requestId, int assignedId) throws SQLException {
        String sql = "update pending_registrations set status = ?, assigned_id = ? where request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, RegistrationStatus.APPROVED.toDbValue());
            stmt.setInt(2, assignedId);
            stmt.setInt(3, requestId);
            stmt.executeUpdate();
        }
    }

    private Registration buildRegistration(ResultSet rs) throws SQLException {
        Registration r = new Registration();
        r.setRequestId(rs.getInt("request_id"));
        r.setName(rs.getString("name"));
        r.setAddress(rs.getString("address"));
        r.setRole(Role.fromDbValue(rs.getInt("role")));
        r.setPassword(rs.getString("password"));
        r.setStatus(RegistrationStatus.fromDbValue(rs.getInt("status")));
        r.setReason(rs.getString("reason"));
        int assignedId = rs.getInt("assigned_id");
        r.setAssignedId(rs.wasNull() ? null : assignedId);
        r.setDegree(rs.getString("degree"));
        int courseId = rs.getInt("course_id");
        r.setCourseId(rs.wasNull() ? null : courseId);
        return r;
    }
}