package Service;

import DAO.CourseDAO;
import DAO.EnrollmentDAO;
import model.Course;
import model.Enrollment;
import Status.EnrollmentStatus;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AcademicPlanService {
    private CourseDAO courseDAO = new CourseDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public List<Course> getCompletedCourses(int studentId) throws SQLException {
        List<Enrollment> history = enrollmentDAO.findByStudent(studentId);
        List<Course> completed = new ArrayList<>();

        for (Enrollment e : history) {
            if (e.getStatus() == EnrollmentStatus.COMPLETED) {
                completed.add(courseDAO.findById(e.getCourseId()));
            }
        }
        return completed;
    }

    public List<Course> getRemainingCourses(int studentId) throws SQLException {
        List<Enrollment> history = enrollmentDAO.findByStudent(studentId);
        List<Course> allCourses = courseDAO.findAll();
        List<Course> remaining = new ArrayList<>();

        for (Course c : allCourses) {
            boolean doneOrInProgress = false;
            for (Enrollment e : history) {
                if (e.getCourseId() == c.getCourseId()
                        && (e.getStatus() == EnrollmentStatus.COMPLETED
                        || e.getStatus() == EnrollmentStatus.ENROLLED)) {
                    doneOrInProgress = true;
                    break;
                }
            }
            if (!doneOrInProgress) remaining.add(c);
        }
        return remaining;
    }
}