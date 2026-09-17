package DAO;

import DatabaseConnection.DBConnection;
import java.sql.*;

public class UsersDAO {


    public int insert(int roleValue, String password, String name, String address) throws SQLException {
        String sql = "insert into users (role, password, name, address) values (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, roleValue);
            stmt.setString(2, password);
            stmt.setString(3, name);
            stmt.setString(4, address);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public void updateProfile(int id, String name, String address) throws SQLException {
        String sql = "update users set name = ?, address = ? where id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "delete from users where id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}