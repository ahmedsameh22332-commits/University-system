package Api;

import DAO.AdminDAO;
import DAO.ProfessorDAO;
import DAO.StudentDAO;
import Service.AdminService;
import Status.AdminLevel;
import Status.Role;
import model.Admin;
import model.Professor;
import model.Registration;
import model.Student;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminRoutes {
    private static final AdminService adminService = new AdminService();
    private static final AdminDAO adminDAO = new AdminDAO();
    private static final StudentDAO studentDAO = new StudentDAO();
    private static final ProfessorDAO professorDAO = new ProfessorDAO();

    public static void register(Javalin app) {
        app.get("/api/admin/registrations/pending", ctx -> {
            List<Registration> pending = adminService.viewPendingRegistrations();
            ctx.json(pending);
        });

        app.post("/api/admin/registrations/{id}/approve", ctx -> {
            int requestId = Integer.parseInt(ctx.pathParam("id"));
            adminService.approveRegistration(requestId);
            ctx.json(Map.of("message", "Approved."));
        });

        app.post("/api/admin/registrations/{id}/reject", ctx -> {
            int requestId = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String reason = JsonUtil.strVal(body, "reason");
            adminService.rejectRegistration(requestId, reason);
            ctx.json(Map.of("message", "Rejected."));
        });

        app.get("/api/admin/users", ctx -> {
            List<Student> students = adminService.getAllStudents();
            List<Professor> professors = adminService.getAllProfessors();
            List<Admin> admins = adminService.getAllAdmins();

            Map<String, Object> response = new HashMap<>();
            response.put("students", students);
            response.put("professors", professors);
            response.put("admins", admins);
            ctx.json(response);
        });

        app.post("/api/admin/users", ctx -> {
            int callingAdminId = requireCallingAdminId(ctx);
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            int roleInput = JsonUtil.intVal(body, "role");
            if (roleInput < 1 || roleInput > 3) {
                throw new IllegalArgumentException("role must be 1 (student), 2 (professor), or 3 (admin)");
            }
            Role role = Role.fromDbValue(roleInput);
            String name = JsonUtil.strVal(body, "name");
            String address = JsonUtil.strVal(body, "address");
            String password = JsonUtil.strVal(body, "password");

            int newId;
            if (role == Role.STUDENT) {
                float gpa = JsonUtil.floatVal(body, "gpa");
                newId = adminService.insertStudent(name, address, password, gpa);
            } else if (role == Role.PROFESSOR) {
                String degree = JsonUtil.strVal(body, "degree");
                newId = adminService.insertProfessor(name, address, password, degree);
            } else {
                int levelInput = JsonUtil.intVal(body, "adminLevel");
                if (levelInput < 1 || levelInput > 2) {
                    throw new IllegalArgumentException("adminLevel must be 1 (Super Admin) or 2 (Regular Admin)");
                }
                AdminLevel level = AdminLevel.fromDbValue(levelInput);
                newId = adminService.insertAdmin(callingAdminId, name, address, password, level);
            }

            ctx.status(201).json(Map.of("message", "Inserted.", "id", newId));
        });

        app.put("/api/admin/users/{id}", ctx -> {
            int callingAdminId = requireCallingAdminId(ctx);
            int targetId = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String name = JsonUtil.strVal(body, "name");
            String address = JsonUtil.strVal(body, "address");

            boolean isStudent = studentDAO.findById(targetId) != null;
            boolean isProfessor = professorDAO.findById(targetId) != null;
            boolean isTargetAdmin = adminDAO.isAdmin(targetId);

            if (!isStudent && !isProfessor && !isTargetAdmin) {
                ctx.status(404).json(Map.of("error", "no such student, professor, or admin"));
                return;
            }

            if (isStudent) {
                float gpa = JsonUtil.floatVal(body, "gpa");
                adminService.updateStudent(targetId, name, address, gpa);
            } else if (isProfessor) {
                String degree = JsonUtil.strVal(body, "degree");
                adminService.updateProfessor(targetId, name, address, degree);
            } else {
                int levelInput = JsonUtil.intVal(body, "adminLevel");
                if (levelInput < 1 || levelInput > 2) {
                    throw new IllegalArgumentException("adminLevel must be 1 (Super Admin) or 2 (Regular Admin)");
                }
                AdminLevel level = AdminLevel.fromDbValue(levelInput);
                adminService.updateAdmin(callingAdminId, targetId, name, address, level);
            }

            ctx.json(Map.of("message", "Updated."));
        });

        app.delete("/api/admin/users/{id}", ctx -> {
            int callingAdminId = requireCallingAdminId(ctx);
            int targetId = Integer.parseInt(ctx.pathParam("id"));
            adminService.deleteUser(callingAdminId, targetId);
            ctx.json(Map.of("message", "Deleted."));
        });
    }

    private static int requireCallingAdminId(Context ctx) {
        String raw = ctx.queryParam("callingAdminId");
        if (raw == null) {
            throw new IllegalArgumentException("callingAdminId query parameter is required");
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("callingAdminId must be a number");
        }
    }
}