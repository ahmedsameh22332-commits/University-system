package Service;
import DAO.*;
import model.*;
import Status.AdminLevel;
import Status.Role;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
public class AdminService {
    private StudentDAO studentDAO = new StudentDAO();
    private ProfessorDAO professorDAO = new ProfessorDAO();
    private AdminDAO adminDAO = new AdminDAO();
    private RegistrationDAO registrationDAO = new RegistrationDAO();
    private UsersDAO usersDAO = new UsersDAO();
    private CourseDAO courseDAO = new CourseDAO();

    public void deleteUser(int callingAdminId, int targetId) throws SQLException {
        if (adminDAO.isAdmin(targetId)) {
            AdminLevel callerLevel = adminDAO.getAdminLevel(callingAdminId);
            if (callerLevel != AdminLevel.SUPER_ADMIN) {
                throw new IllegalStateException("only a super admin can delete an admin");
            }
            AdminLevel targetLevel = adminDAO.getAdminLevel(targetId);
            if (targetLevel == AdminLevel.SUPER_ADMIN) {
                throw new IllegalStateException("cannot delete a super admin");
            }
            Connection conn = DatabaseConnection.DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("delete from admin where id = ?");
            stmt.setInt(1, targetId);
            stmt.executeUpdate();
            usersDAO.delete(targetId);
            return;
        }
        studentDAO.delete(targetId);
        professorDAO.delete(targetId);
        usersDAO.delete(targetId);
    }

    public void approveRegistration(int requestId) throws SQLException {
        Registration r = registrationDAO.findById(requestId);
        if (r == null) throw new IllegalArgumentException("no such request");

        int newId = usersDAO.insert(r.getRole().toDbValue(), r.getPassword(), r.getName(), r.getAddress());

        if (r.getRole() == Role.STUDENT) {
            studentDAO.insert(newId, 0f);
        } else if (r.getRole() == Role.PROFESSOR) {
            professorDAO.insert(newId, r.getDegree() == null ? "" : r.getDegree());
            if (r.getCourseId() != null) {
                courseDAO.assignProfessor(r.getCourseId(), newId);
            }
        }

        registrationDAO.approve(requestId, newId);
        System.out.println("Approved. New user id: " + newId);
    }

    public void rejectRegistration(int requestId, String reason) throws SQLException {
        registrationDAO.reject(requestId, reason);
        System.out.println("Rejected.");
    }

    public List<Registration> viewPendingRegistrations() throws SQLException {
        return registrationDAO.findPending();
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public List<Professor> getAllProfessors() throws SQLException {
        return professorDAO.findAll();
    }

    public List<Admin> getAllAdmins() throws SQLException {
        return adminDAO.findAll();
    }

    public int insertStudent(String name, String address, String password, float gpa) throws SQLException {
        int newId = usersDAO.insert(Role.STUDENT.toDbValue(), password, name, address);
        studentDAO.insert(newId, gpa);
        return newId;
    }

    public int insertProfessor(String name, String address, String password, String degree) throws SQLException {
        int newId = usersDAO.insert(Role.PROFESSOR.toDbValue(), password, name, address);
        professorDAO.insert(newId, degree);
        return newId;
    }

    public int insertAdmin(int callingAdminId, String name, String address, String password, AdminLevel adminLevel) throws SQLException {
        AdminLevel callerLevel = adminDAO.getAdminLevel(callingAdminId);
        if (callerLevel != AdminLevel.SUPER_ADMIN) {
            throw new IllegalStateException("only a super admin can add an admin");
        }
        int newId = usersDAO.insert(Role.ADMIN.toDbValue(), password, name, address);
        adminDAO.insert(newId, adminLevel.toDbValue());
        return newId;
    }

    public void updateStudent(int id, String name, String address, float gpa) throws SQLException {
        usersDAO.updateProfile(id, name, address);
        studentDAO.updateGpa(id, gpa);
    }

    public void updateProfessor(int id, String name, String address, String degree) throws SQLException {
        usersDAO.updateProfile(id, name, address);
        professorDAO.updateDegree(id, degree);
    }

    public void updateAdmin(int callingAdminId, int targetId, String name, String address, AdminLevel adminLevel) throws SQLException {
        AdminLevel callerLevel = adminDAO.getAdminLevel(callingAdminId);
        if (callerLevel != AdminLevel.SUPER_ADMIN) {
            throw new IllegalStateException("only a super admin can update an admin");
        }
        usersDAO.updateProfile(targetId, name, address);
        adminDAO.updateAdminLevel(targetId, adminLevel.toDbValue());
    }
}