package menu;

import DAO.ProfessorDAO;
import DAO.UsersDAO;
import Service.EnrollmentService;
import Status.EnrollmentStatus;
import model.Professor;
import model.Student;

import java.sql.SQLException;
import java.util.List;

import static menu.InputHelper.*;

public class ProfessorMenu {

    private final EnrollmentService enrollmentService;
    private final ProfessorDAO professorDAO;
    private final UsersDAO usersDAO;
    private final String currentTerm;

    public ProfessorMenu(EnrollmentService enrollmentService, ProfessorDAO professorDAO,
                          UsersDAO usersDAO, String currentTerm) {
        this.enrollmentService = enrollmentService;
        this.professorDAO = professorDAO;
        this.usersDAO = usersDAO;
        this.currentTerm = currentTerm;
    }

    public void show(int professorId) {
        try {
            while (true) {
                int choice = readIntChoice("\n1. View my profile  2. Update my profile  3. View my students" +
                        "  4. Update a student's grade  5. Back\n");

                if (choice == 1) {
                    professorDAO.findById(professorId).display();
                } else if (choice == 2) {
                    Professor p = professorDAO.findById(professorId);
                    String name = readLine("New name (" + p.getName() + "): ");
                    String address = readLine("New address (" + p.getAddress() + "): ");
                    String degree = readLine("New degree (" + p.getDegree() + "): ");
                    usersDAO.updateProfile(professorId, name, address);
                    professorDAO.updateDegree(professorId, degree);
                    System.out.println("Profile updated.");
                } else if (choice == 3) {
                    List<Student> students = enrollmentService.getStudentsForProfessor(professorId);
                    if (students.isEmpty()) System.out.println("No students enrolled in your courses.");
                    for (Student s : students) {
                        System.out.println(s.getId() + " - " + s.getName() + " (GPA " + s.getGpa() + ")");
                    }
                } else if (choice == 4) {
                    int studentId = readIntChoice("Student id: ");
                    int courseId = readIntChoice("Course id: ");
                    float grade = readFloatInRange("New grade (0 - 100): ", "Grade must be between 0 and 100.", 0.0f, 100.0f);
                    int statusChoice = readIntInRange("Status: 1=Enrolled 2=Completed 3=Failed 4=Dropped\n",
                            "Please enter 1, 2, 3, or 4.", 1, 4);
                    EnrollmentStatus status = EnrollmentStatus.fromDbValue(statusChoice);
                    try {
                        enrollmentService.updateGrade(professorId, studentId, courseId, currentTerm, grade, status);
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        System.out.println("Update failed: " + e.getMessage());
                    }
                } else if (choice == 5) {
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
