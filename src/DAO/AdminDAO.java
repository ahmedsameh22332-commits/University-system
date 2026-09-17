package DAO;

import DatabaseConnection.DBConnection;
import model.Admin;
import Status.AdminLevel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public Admin findById(int id) throws SQLException {
        String sql = "select u.name, u.address, a.admin_level from users u join admin a on u.id = a.id where u.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return buildAdmin(rs, id);
            }
        }
    }

    public List<Admin> findAll() throws SQLException {
        String sql = "select u.id, u.name, u.address, a.admin_level from users u join admin a on u.id = a.id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Admin> list = new ArrayList<>();
            while (rs.next()) {
                list.add(buildAdmin(rs, rs.getInt("id")));
            }
            return list;
        }
    }

    public AdminLevel getAdminLevel(int id) throws SQLException {
        String sql = "select admin_level from admin where id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new IllegalArgumentException("no such admin");
                return AdminLevel.fromDbValue(rs.getInt("admin_level"));
            }
        }
    }

    public boolean isAdmin(int id) throws SQLException {
        String sql = "select id from admin where id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void insert(int id, int adminLevel) throws SQLException {
        String sql = "insert into admin (id, admin_level) values (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, adminLevel);
            stmt.executeUpdate();
        }
    }

    public void updateAdminLevel(int id, int adminLevel) throws SQLException {
        String sql = "update admin set admin_level = ? where id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adminLevel);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    private Admin buildAdmin(ResultSet rs, int id) throws SQLException {
        Admin a = new Admin();
        a.setId(id);
        a.setName(rs.getString("name"));
        a.setAddress(rs.getString("address"));
        a.setAdminLevel(AdminLevel.fromDbValue(rs.getInt("admin_level")));
        return a;
    }
}