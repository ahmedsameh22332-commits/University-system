package DAO;

import DatabaseConnection.DBConnection;
import model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public Course findById(int courseId) throws SQLException {
        String sql = "select * from courses where course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return buildCourse(rs);
            }
        }
    }

    public List<Course> findAll() throws SQLException {
        String sql = "select * from courses";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Course> courses = new ArrayList<>();
            while (rs.next()) {
                courses.add(buildCourse(rs));
            }
            return courses;
        }
    }

    public List<Integer> findPrerequisites(int courseId) throws SQLException {
        String sql = "select prerequisite_course_id from prerequisites where course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Integer> prereqs = new ArrayList<>();
                while (rs.next()) {
                    prereqs.add(rs.getInt("prerequisite_course_id"));
                }
                return prereqs;
            }
        }
    }

    public void assignProfessor(int courseId, int professorId) throws SQLException {
        String sql = "update courses set professor_id = ? where course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, professorId);
            stmt.setInt(2, courseId);
            stmt.executeUpdate();
        }
    }

    private Course buildCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseName(rs.getString("course_name"));
        c.setProfessorId(rs.getInt("professor_id"));
        c.setMinGpaRequired(rs.getFloat("min_gpa_required"));
        c.setCreditHours(rs.getInt("credit_hours"));
        return c;
    }
}