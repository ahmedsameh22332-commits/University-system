package menu;

import DAO.CourseDAO;
import DAO.RegistrationDAO;
import Service.Login;
import Status.RegistrationStatus;
import Status.Role;
import model.Course;
import model.Registration;

import java.sql.SQLException;

import static menu.InputHelper.*;


public class AuthMenu {

    private final Login auth;
    private final RegistrationDAO registrationDAO;
    private final CourseDAO courseDAO;
    private final StudentMenu studentMenu;
    private final ProfessorMenu professorMenu;
    private final AdminMenu adminMenu;

    public AuthMenu(Login auth, RegistrationDAO registrationDAO, CourseDAO courseDAO,
                     StudentMenu studentMenu, ProfessorMenu professorMenu, AdminMenu adminMenu) {
        this.auth = auth;
        this.registrationDAO = registrationDAO;
        this.courseDAO = courseDAO;
        this.studentMenu = studentMenu;
        this.professorMenu = professorMenu;
        this.adminMenu = adminMenu;
    }

    public void login() {
        try {
            int id = readIntChoice("Enter id: ");
            String password = readLine("Enter password: ");

            Role role = auth.authenticate(id, password);
            System.out.println("Login successful. Role: " + role);

            if (role == Role.STUDENT) studentMenu.show(id);
            else if (role == Role.PROFESSOR) professorMenu.show(id);
            else if (role == Role.ADMIN) adminMenu.show(id);

        } catch (IllegalArgumentException e) {
            System.out.println("Login failed: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkRegistrationStatus() {
        try {
            String name = readLine("Enter the name you registered with: ");
            String password = readLine("Enter your password: ");

            Registration r = registrationDAO.findByNameAndPassword(name, password);
            if (r == null) {
                System.out.println("No matching registration found.");
                return;
            }

            if (r.getStatus() == RegistrationStatus.PENDING) {
                System.out.println("Your registration is still pending review.");
            } else if (r.getStatus() == RegistrationStatus.REJECTED) {
                System.out.println("Your registration was rejected. Reason: " + r.getReason());
                int retry = readIntChoice("Would you like to register again? 1. Yes  2. No\n");
                if (retry == 1) registerRequest();
            } else if (r.getStatus() == RegistrationStatus.APPROVED) {
                System.out.println("Congratulations! You are now a " + r.getRole()
                        + ". Your id is " + r.getAssignedId() + ". Use it to log in.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registerRequest() {
        try {
            String name = readLine("Enter name: ");
            validateName(name);

            String address = readLine("Enter address: ");

            int roleInput = readIntInRange("Enter role (1=student, 2=professor, 3=admin): ",
                    "Please enter 1, 2, or 3.", 1, 3);
            Role role = Role.fromDbValue(roleInput);

            String degree = null;
            Integer courseId = null;
            if (role == Role.PROFESSOR) {
                degree = readLine("Enter degree: ");

                System.out.println("Available courses:");
                try {
                    for (Course c : courseDAO.findAll()) {
                        System.out.println(" " + c.getCourseId() + ": " + c.getCourseName());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                courseId = readIntChoice("Course id you will teach: ");
            }

            String password = readLine("Enter password: ");

            Registration reg = new Registration();
            reg.setName(name);
            reg.setAddress(address);
            reg.setRole(role);
            reg.setPassword(password);
            reg.setStatus(RegistrationStatus.PENDING);
            reg.setDegree(degree);
            reg.setCourseId(courseId);

            registrationDAO.insert(reg);
            System.out.println("Registration submitted. Waiting for admin approval.");

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("empty field");
        if (name.matches(".*\\d.*"))
            throw new IllegalArgumentException("no numbers allowed");
        if (!name.matches("[a-zA-Z ]+"))
            throw new IllegalArgumentException("no special characters");
    }
}
