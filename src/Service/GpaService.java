package Service;

import DAO.CourseDAO;
import DAO.EnrollmentDAO;
import DAO.StudentDAO;
import model.Course;
import model.Enrollment;
import Status.EnrollmentStatus;
import java.sql.SQLException;
import java.util.List;

public class GpaService {
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private StudentDAO studentDAO = new StudentDAO();

    public static float toGradePoint(float score) {
        if (score >= 90) return 4.0f;
        if (score >= 85) return 3.7f;
        if (score >= 80) return 3.5f;
        if (score >= 75) return 3.3f;
        if (score >= 70) return 3.0f;
        if (score >= 65) return 2.7f;
        if (score >= 60) return 2.5f;
        if (score >= 55) return 2.0f;
        if (score >= 50) return 1.0f;
        return 0.0f;
    }

    public float calculateGpa(int studentId) throws SQLException {
        List<Enrollment> history = enrollmentDAO.findByStudent(studentId);

        float totalPoints = 0f;
        int totalHours = 0;

        for (Enrollment e : history) {
            if (e.getStatus() != EnrollmentStatus.COMPLETED) continue;

            Course c = courseDAO.findById(e.getCourseId());
            if (c == null) continue;

            totalPoints += toGradePoint(e.getGrade()) * c.getCreditHours();
            totalHours += c.getCreditHours();
        }

        if (totalHours == 0) return 0f;
        return totalPoints / totalHours;
    }

    public void recalculateAndUpdateGpa(int studentId) throws SQLException {
        float gpa = calculateGpa(studentId);
        studentDAO.updateGpa(studentId, gpa);
    }
}