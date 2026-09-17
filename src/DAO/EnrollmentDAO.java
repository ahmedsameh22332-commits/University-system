package DAO;

import DatabaseConnection.DBConnection;
import model.Enrollment;
import model.Student;
import Status.EnrollmentStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public List<Enrollment> findByStudent(int studentId) throws SQLException {
        String sql = "select * from enrollments where student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Enrollment> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(buildEnrollment(rs));
                }
                return list;
            }
        }
    }

    public List<Student> findStudentsByProfessor(int professorId) throws SQLException {
        String sql = "select distinct u.id, u.name, u.address, s.gpa " +
                "from enrollments e " +
                "join courses c on e.course_id = c.course_id " +
                "join students s on e.student_id = s.id " +
                "join users u on s.id = u.id " +
                "where c.professor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, professorId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Student> list = new ArrayList<>();
                while (rs.next()) {
                    Student s = new Student();
                    s.setId(rs.getInt("id"));
                    s.setName(rs.getString("name"));
                    s.setAddress(rs.getString("address"));
                    s.setGpa(rs.getFloat("gpa"));
                    list.add(s);
                }
                return list;
            }
        }
    }

    public void updateGrade(int studentId, int courseId, String term, float grade, EnrollmentStatus status) throws SQLException {
        String sql = "update enrollments set grade = ?, status = ? where student_id = ? and course_id = ? and term = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, grade);
            stmt.setInt(2, status.toDbValue());
            stmt.setInt(3, studentId);
            stmt.setInt(4, courseId);
            stmt.setString(5, term);
            stmt.executeUpdate();
        }
    }

    public int countCurrentTermEnrollments(int studentId, String term) throws SQLException {
        String sql = "select count(*) as total from enrollments where student_id = ? and term = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, term);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("total");
            }
        }
    }

    public boolean hasActiveOrCompletedEnrollment(int studentId, int courseId) throws SQLException {
        String sql = "select * from enrollments where student_id = ? and course_id = ? and status in (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setInt(3, EnrollmentStatus.ENROLLED.toDbValue());
            stmt.setInt(4, EnrollmentStatus.COMPLETED.toDbValue());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void insert(Enrollment e) throws SQLException {
        String sql = "insert into enrollments (student_id, course_id, term, grade, status) values (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, e.getStudentId());
            stmt.setInt(2, e.getCourseId());
            stmt.setString(3, e.getTerm());
            stmt.setFloat(4, e.getGrade());
            stmt.setInt(5, e.getStatus().toDbValue());
            stmt.executeUpdate();
        }
    }

    private Enrollment buildEnrollment(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setStudentId(rs.getInt("student_id"));
        e.setCourseId(rs.getInt("course_id"));
        e.setTerm(rs.getString("term"));
        e.setGrade(rs.getFloat("grade"));
        e.setStatus(EnrollmentStatus.fromDbValue(rs.getInt("status")));
        return e;
    }
}