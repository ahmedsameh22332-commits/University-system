package menu;

import DAO.StudentDAO;
import Service.AcademicPlanService;
import Service.EnrollmentService;
import model.Course;

import java.sql.SQLException;
import java.util.List;

import static menu.InputHelper.readIntChoice;

public class StudentMenu {

    private final EnrollmentService enrollmentService;
    private final AcademicPlanService planService;
    private final StudentDAO studentDAO;
    private final String currentTerm;

    public StudentMenu(EnrollmentService enrollmentService, AcademicPlanService planService,
                        StudentDAO studentDAO, String currentTerm) {
        this.enrollmentService = enrollmentService;
        this.planService = planService;
        this.studentDAO = studentDAO;
        this.currentTerm = currentTerm;
    }

    public void show(int studentId) {
        try {
            while (true) {
                int choice = readIntChoice("\n1. View my profile  2. View academic plan  3. courses you are able to enroll" +
                        "  4. View my enrollments  5. Enroll in course  6. Back\n");

                if (choice == 1) {
                    studentDAO.findById(studentId).display();
                } else if (choice == 2) {
                    List<Course> completed = planService.getCompletedCourses(studentId);
                    List<Course> remaining = planService.getRemainingCourses(studentId);
                    System.out.println("Completed:");
                    for (Course c : completed) System.out.println(" - " + c.getCourseId() + ": " + c.getCourseName());
                    System.out.println("Remaining:");
                    for (Course c : remaining) System.out.println(" - " + c.getCourseId() + ": " + c.getCourseName());
                } else if (choice == 3) {
                    List<Course> eligible = enrollmentService.getEligibleCourses(studentId);
                    if (eligible.isEmpty()) {
                        System.out.println("No courses currently meet your GPA.");
                    }
                    for (Course c : eligible) {
                        System.out.println(c.getCourseId() + ": " + c.getCourseName()
                                + " (min GPA " + c.getMinGpaRequired() + ")");
                    }
                } else if (choice == 4) {
                    enrollmentService.viewMyEnrollments(studentId);
                } else if (choice == 5) {
                    int courseId = readIntChoice("Course id: ");
                    try {
                        enrollmentService.enroll(studentId, courseId, currentTerm);
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        System.out.println("Enrollment failed: " + e.getMessage());
                    }
                } else if (choice == 6) {
                    break;
                } else {
                    System.out.println("Invalid option.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
