package menu;

import DAO.*;
import Service.*;

import static menu.InputHelper.readIntChoice;


public class MainMenu {

    private static final String CURRENT_TERM = "Fall2026"; // update each semester

    private final AuthMenu authMenu;

    public MainMenu() {
        EnrollmentService enrollmentService = new EnrollmentService();
        AcademicPlanService planService = new AcademicPlanService();
        AdminService adminService = new AdminService();
        RegistrationDAO registrationDAO = new RegistrationDAO();
        StudentDAO studentDAO = new StudentDAO();
        ProfessorDAO professorDAO = new ProfessorDAO();
        AdminDAO adminDAO = new AdminDAO();
        UsersDAO usersDAO = new UsersDAO();
        CourseDAO courseDAO = new CourseDAO();
        Login auth = new Login();

        StudentMenu studentMenu = new StudentMenu(enrollmentService, planService, studentDAO, CURRENT_TERM);
        ProfessorMenu professorMenu = new ProfessorMenu(enrollmentService, professorDAO, usersDAO, CURRENT_TERM);
        AdminMenu adminMenu = new AdminMenu(adminService, studentDAO, professorDAO, adminDAO);

        this.authMenu = new AuthMenu(auth, registrationDAO, courseDAO, studentMenu, professorMenu, adminMenu);
    }

    public void run() {
        while (true) {
            int choice = readIntChoice("\n1. Login  2. Register  3. Check registration status  4. Exit\n");
            if (choice == 1) authMenu.login();
            else if (choice == 2) authMenu.registerRequest();
            else if (choice == 3) authMenu.checkRegistrationStatus();
            else if (choice == 4) break;
            else System.out.println("Invalid option.");
        }
    }
}
