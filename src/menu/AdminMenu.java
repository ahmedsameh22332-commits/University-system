package menu;

import DAO.AdminDAO;
import DAO.ProfessorDAO;
import DAO.StudentDAO;
import Service.AdminService;
import Status.AdminLevel;
import model.Admin;
import model.Professor;
import model.Registration;
import model.Student;

import java.sql.SQLException;
import java.util.List;

import static menu.InputHelper.*;

public class AdminMenu {

    private final AdminService adminService;
    private final StudentDAO studentDAO;
    private final ProfessorDAO professorDAO;
    private final AdminDAO adminDAO;

    public AdminMenu(AdminService adminService, StudentDAO studentDAO,
                      ProfessorDAO professorDAO, AdminDAO adminDAO) {
        this.adminService = adminService;
        this.studentDAO = studentDAO;
        this.professorDAO = professorDAO;
        this.adminDAO = adminDAO;
    }

    public void show(int adminId) {
        try {
            while (true) {
                int choice = readIntChoice("\n1. Manage pending registrations  2. View all users  3. Insert user" +
                        "  4. Update user  5. Delete user  6. Back\n");

                if (choice == 1) {
                    managePendingRegistrations();
                } else if (choice == 2) {
                    System.out.println("Students:");
                    for (Student s : adminService.getAllStudents())
                        System.out.println(" " + s.getId() + " - " + s.getName());
                    System.out.println("Professors:");
                    for (Professor p : adminService.getAllProfessors())
                        System.out.println(" " + p.getId() + " - " + p.getName());
                    System.out.println("Admins:");
                    for (Admin a : adminService.getAllAdmins())
                        System.out.println(" " + a.getId() + " - " + a.getName() + " (" + a.getAdminLevel() + ")");
                } else if (choice == 3) {
                    insertUserFlow(adminId);
                } else if (choice == 4) {
                    updateUserFlow(adminId);
                } else if (choice == 5) {
                    int targetId = readIntChoice("Target user id: ");
                    try {
                        adminService.deleteUser(adminId, targetId);
                        System.out.println("Deleted.");
                    } catch (IllegalStateException e) {
                        System.out.println("Delete failed: " + e.getMessage());
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

    private void managePendingRegistrations() throws SQLException {
        List<Registration> pending = adminService.viewPendingRegistrations();
        if (pending.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }
        for (Registration r : pending) {
            System.out.println(r.getRequestId() + " - " + r.getName() + " (" + r.getRole() + ")");
        }

        int reqId = readIntChoice("Enter request id to act on (or 0 to go back): ");
        if (reqId == 0) return;

        int action = readIntChoice("1. Approve  2. Reject  3. Back\n");
        if (action == 1) {
            adminService.approveRegistration(reqId);
        } else if (action == 2) {
            int reasonChoice = readIntChoice(
                    "Reason:\n1. Invalid or inappropriate name\n2. Incorrect role selection\n" +
                            "3. Incomplete or invalid address\n4. Duplicate - already has an account\n" +
                            "5. Suspected fraudulent request\n6. Other\n");
            String reason;
            if (reasonChoice == 1) reason = "Invalid or inappropriate name";
            else if (reasonChoice == 2) reason = "Incorrect role selection";
            else if (reasonChoice == 3) reason = "Incomplete or invalid address";
            else if (reasonChoice == 4) reason = "Duplicate - already has an account";
            else if (reasonChoice == 5) reason = "Suspected fraudulent request";
            else reason = readLine("Enter custom reason: ");
            adminService.rejectRegistration(reqId, reason);
        }
    }

    private void insertUserFlow(int callingAdminId) throws SQLException {
        int roleChoice = readIntInRange("Insert: 1=Student  2=Professor  3=Admin\n",
                "Please enter 1, 2, or 3.", 1, 3);
        String name = readLine("Name: ");
        String address = readLine("Address: ");
        String password = readLine("Password: ");

        if (roleChoice == 1) {
            float gpa = readFloatInRange("GPA: ", "GPA must be between 0.0 and 4.0.", 0.0f, 4.0f);
            int newId = adminService.insertStudent(name, address, password, gpa);
            System.out.println("Inserted. New id: " + newId);
        } else if (roleChoice == 2) {
            String degree = readLine("Degree: ");
            int newId = adminService.insertProfessor(name, address, password, degree);
            System.out.println("Inserted. New id: " + newId);
        } else {
            AdminLevel level = AdminLevel.fromDbValue(
                    readIntInRange("Admin level (1=Super Admin, 2=Regular Admin): ", "Please enter 1 or 2.", 1, 2));
            try {
                int newId = adminService.insertAdmin(callingAdminId, name, address, password, level);
                System.out.println("Inserted. New id: " + newId);
            } catch (IllegalStateException e) {
                System.out.println("Insert failed: " + e.getMessage());
            }
        }
    }

    private void updateUserFlow(int callingAdminId) throws SQLException {
        int targetId = readIntChoice("User id to update: ");

        boolean isStudent = studentDAO.findById(targetId) != null;
        boolean isProfessor = professorDAO.findById(targetId) != null;
        boolean isTargetAdmin = adminDAO.isAdmin(targetId);

        if (!isStudent && !isProfessor && !isTargetAdmin) {
            System.out.println("No such student, professor, or admin with that id.");
            return;
        }

        String name = readLine("New name: ");
        String address = readLine("New address: ");

        if (isStudent) {
            float gpa = readFloatInRange("New GPA: ", "GPA must be between 0.0 and 4.0.", 0.0f, 4.0f);
            adminService.updateStudent(targetId, name, address, gpa);
        } else if (isProfessor) {
            String degree = readLine("New degree: ");
            adminService.updateProfessor(targetId, name, address, degree);
        } else {
            AdminLevel level = AdminLevel.fromDbValue(
                    readIntInRange("New admin level (1=Super Admin, 2=Regular Admin): ", "Please enter 1 or 2.", 1, 2));
            try {
                adminService.updateAdmin(callingAdminId, targetId, name, address, level);
            } catch (IllegalStateException e) {
                System.out.println("Update failed: " + e.getMessage());
                return;
            }
        }
        System.out.println("Updated.");
    }
}
