package DAO;

import DatabaseConnection.DBConnection;
import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public Student findById(int id) throws SQLException {
        String sql = "select u.name, u.address, s.gpa from users u join students s on u.id = s.id where u.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return buildStudent(rs, id);
            }
        }
    }

    public List<Student> findAll() throws SQLException {
        String sql = "select u.id, u.name, u.address, s.gpa from users u join students s on u.id = s.id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Student> list = new ArrayList<>();
            while (rs.next()) {
                list.add(buildStudent(rs, rs.getInt("id")));
            }
            return list;
        }
    }

    public void updateGpa(int studentId, float newGpa) throws SQLException {
        String sql = "update students set gpa = ? where id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, newGpa);
            stmt.setInt(2, studentId);
            stmt.executeUpdate();
        }
    }

    public void insert(int id, float gpa) throws SQLException {
        String sql = "insert into students (id, gpa) values (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setFloat(2, gpa);
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "delete from students where id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Student buildStudent(ResultSet rs, int id) throws SQLException {
        Student s = new Student();
        s.setId(id);
        s.setName(rs.getString("name"));
        s.setAddress(rs.getString("address"));
        s.setGpa(rs.getFloat("gpa"));
        return s;
    }
}