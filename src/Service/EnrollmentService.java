package Service;

import DAO.CourseDAO;
import DAO.EnrollmentDAO;
import DAO.StudentDAO;
import model.Course;
import model.Enrollment;
import model.Student;
import Status.EnrollmentStatus;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLIntegrityConstraintViolationException;

public class EnrollmentService {
    private StudentDAO studentDAO = new StudentDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private GpaService gpaService = new GpaService();

    public int getMaxAllowedCourses(float gpa) {
        if (gpa < 2.5) return 5;
        if (gpa < 3.5) return 6;
        return 7;
    }

    public boolean prerequisitesMet(int studentId, int courseId) throws SQLException {
        List<Integer> prereqs = courseDAO.findPrerequisites(courseId);
        List<Enrollment> history = enrollmentDAO.findByStudent(studentId);

        for (int prereqId : prereqs) {
            boolean passed = false;
            for (Enrollment e : history) {
                if (e.getCourseId() == prereqId
                        && e.getStatus() == EnrollmentStatus.COMPLETED
                        && GpaService.toGradePoint(e.getGrade()) >= 2.0) {
                    passed = true;
                    break;
                }
            }
            if (!passed) return false;
        }
        return true;
    }

    public void checkPrerequisites(int studentId, int courseId) throws SQLException {
        if (!prerequisitesMet(studentId, courseId)) {
            throw new IllegalStateException("prerequisite not met for course " + courseId);
        }
    }

    public List<Course> getEligibleCourses(int studentId) throws SQLException {
        Student student = studentDAO.findById(studentId);
        if (student == null) throw new IllegalArgumentException("no such student");

        List<Course> eligible = new ArrayList<>();
        for (Course c : courseDAO.findAll()) {
            if (enrollmentDAO.hasActiveOrCompletedEnrollment(studentId, c.getCourseId())) continue;
            if (student.getGpa() < c.getMinGpaRequired()) continue;
            if (!prerequisitesMet(studentId, c.getCourseId())) continue;
            eligible.add(c);
        }
        return eligible;
    }

    public void enroll(int studentId, int courseId, String term) throws SQLException {
        Student student = studentDAO.findById(studentId);
        Course course = courseDAO.findById(courseId);

        if (student == null) throw new IllegalArgumentException("no such student");
        if (course == null) throw new IllegalArgumentException("no such course");

        if (enrollmentDAO.hasActiveOrCompletedEnrollment(studentId, courseId)) {
            throw new IllegalStateException("already enrolled in or completed this course");
        }
        if (student.getGpa() < course.getMinGpaRequired()) {
            throw new IllegalStateException("GPA too low for this course");
        }

        checkPrerequisites(studentId, courseId);

        int currentCount = enrollmentDAO.countCurrentTermEnrollments(studentId, term);
        int maxAllowed = getMaxAllowedCourses(student.getGpa());
        if (currentCount >= maxAllowed) {
            throw new IllegalStateException("course load limit reached for this term");
        }

        Enrollment e = new Enrollment();
        e.setStudentId(studentId);
        e.setCourseId(courseId);
        e.setTerm(term);
        e.setGrade(0);
        e.setStatus(EnrollmentStatus.ENROLLED);

        try {
            enrollmentDAO.insert(e);
        } catch (SQLIntegrityConstraintViolationException ex) {
            throw new IllegalStateException("you already have a record for this course this term");
        }

        System.out.println("Enrollment successful.");
    }
    public List<Enrollment> getEnrollmentHistory(int studentId) throws SQLException {
        return enrollmentDAO.findByStudent(studentId);
    }
    public void viewMyEnrollments(int studentId) throws SQLException {
        List<Enrollment> history = enrollmentDAO.findByStudent(studentId);
        if (history.isEmpty()) {
            System.out.println("Not enrolled in any courses.");
            return;
        }
        for (Enrollment e : history) {
            Course c = courseDAO.findById(e.getCourseId());
            System.out.println("Course id " + c.getCourseId() + " - " + c.getCourseName()
                    + " | term: " + e.getTerm() + " | status: " + e.getStatus() + " | grade: " + e.getGrade());
        }
    }

    public List<Student> getStudentsForProfessor(int professorId) throws SQLException {
        return enrollmentDAO.findStudentsByProfessor(professorId);
    }

    public void updateGrade(int professorId, int studentId, int courseId, String term, float grade, EnrollmentStatus status) throws SQLException {
        Course course = courseDAO.findById(courseId);
        if (course == null) throw new IllegalArgumentException("no such course");
        if (course.getProfessorId() != professorId) {
            throw new IllegalStateException("you do not teach this course");
        }
        enrollmentDAO.updateGrade(studentId, courseId, term, grade, status);
        gpaService.recalculateAndUpdateGpa(studentId);
        System.out.println("Grade updated.");
    }
}