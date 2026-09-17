package Api;

import DAO.RegistrationDAO;
import Service.Login;
import Status.Role;
import Status.RegistrationStatus;
import model.Registration;
import io.javalin.Javalin;

import java.util.HashMap;
import java.util.Map;

public class AuthRoutes {
    private static final Login auth = new Login();
    private static final RegistrationDAO registrationDAO = new RegistrationDAO();

    public static void register(Javalin app) {
        app.post("/api/login", ctx -> {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            int id = JsonUtil.intVal(body, "id");
            String password = JsonUtil.strVal(body, "password");

            Role role = auth.authenticate(id, password);

            Map<String, Object> response = new HashMap<>();
            response.put("id", id);
            response.put("role", role.name());
            ctx.json(response);
        });

        app.post("/api/register", ctx -> {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String name = JsonUtil.strVal(body, "name");
            String address = JsonUtil.strVal(body, "address");
            int roleInput = JsonUtil.intVal(body, "role");
            String password = JsonUtil.strVal(body, "password");

            if (roleInput < 1 || roleInput > 3) {
                throw new IllegalArgumentException("role must be 1 (student), 2 (professor), or 3 (admin)");
            }
            Role role = Role.fromDbValue(roleInput);
            validateName(name);

            String degree = null;
            Integer courseId = null;
            if (role == Role.PROFESSOR) {
                degree = JsonUtil.strVal(body, "degree");
                courseId = JsonUtil.intVal(body, "courseId");
            }

            Registration reg = new Registration();
            reg.setName(name);
            reg.setAddress(address);
            reg.setRole(role);
            reg.setPassword(password);
            reg.setStatus(RegistrationStatus.PENDING);
            reg.setDegree(degree);
            reg.setCourseId(courseId);

            registrationDAO.insert(reg);
            ctx.status(201).json(Map.of("message", "Registration submitted. Waiting for admin approval."));
        });

        app.get("/api/registration-status", ctx -> {
            String name = ctx.queryParam("name");
            String password = ctx.queryParam("password");
            if (name == null || password == null) {
                throw new IllegalArgumentException("name and password query parameters are required");
            }

            Registration r = registrationDAO.findByNameAndPassword(name, password);
            if (r == null) {
                ctx.status(404).json(Map.of("error", "no matching registration found"));
                return;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", r.getStatus().name());
            if (r.getStatus() == RegistrationStatus.REJECTED) {
                response.put("reason", r.getReason());
            } else if (r.getStatus() == RegistrationStatus.APPROVED) {
                response.put("role", r.getRole().name());
                response.put("assignedId", r.getAssignedId());
            }
            ctx.json(response);
        });
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("empty field");
        if (name.matches(".*\\d.*"))
            throw new IllegalArgumentException("no numbers allowed");
        if (!name.matches("[a-zA-Z ]+"))
            throw new IllegalArgumentException("no special characters");
    }
}