package DAO;

import DatabaseConnection.DBConnection;
import model.Professor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDAO {

    public Professor findById(int id) throws SQLException {
        String sql = "select u.name, u.address, p.degree from users u join professor p on u.id = p.id where u.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return buildProfessor(rs, id);
            }
        }
    }

    public List<Professor> findAll() throws SQLException {
        String sql = "select u.id, u.name, u.address, p.degree from users u join professor p on u.id = p.id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Professor> list = new ArrayList<>();
            while (rs.next()) {
                list.add(buildProfessor(rs, rs.getInt("id")));
            }
            return list;
        }
    }

    public void updateDegree(int id, String degree) throws SQLException {
        String sql = "update professor set degree = ? where id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, degree);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void insert(int id, String degree) throws SQLException {
        String sql = "insert into professor (id, degree) values (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, degree);
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "delete from professor where id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Professor buildProfessor(ResultSet rs, int id) throws SQLException {
        Professor p = new Professor();
        p.setId(id);
        p.setName(rs.getString("name"));
        p.setAddress(rs.getString("address"));
        p.setDegree(rs.getString("degree"));
        return p;
    }
}