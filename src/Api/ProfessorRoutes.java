package Api;

import DAO.ProfessorDAO;
import DAO.UsersDAO;
import Service.EnrollmentService;
import Status.EnrollmentStatus;
import model.Professor;
import model.Student;
import io.javalin.Javalin;

import java.util.List;
import java.util.Map;

public class ProfessorRoutes {
    private static final ProfessorDAO professorDAO = new ProfessorDAO();
    private static final UsersDAO usersDAO = new UsersDAO();
    private static final EnrollmentService enrollmentService = new EnrollmentService();
    private static final String CURRENT_TERM = "Fall2026"; // keep in sync with Main.java

    public static void register(Javalin app) {
        app.get("/api/professors/{id}", ctx -> {
            int professorId = Integer.parseInt(ctx.pathParam("id"));
            Professor p = professorDAO.findById(professorId);
            if (p == null) {
                ctx.status(404).json(Map.of("error", "no such professor"));
                return;
            }
            ctx.json(p);
        });

        app.put("/api/professors/{id}", ctx -> {
            int professorId = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String name = JsonUtil.strVal(body, "name");
            String address = JsonUtil.strVal(body, "address");
            String degree = JsonUtil.strVal(body, "degree");

            usersDAO.updateProfile(professorId, name, address);
            professorDAO.updateDegree(professorId, degree);
            ctx.json(Map.of("message", "Profile updated."));
        });

        app.get("/api/professors/{id}/students", ctx -> {
            int professorId = Integer.parseInt(ctx.pathParam("id"));
            List<Student> students = enrollmentService.getStudentsForProfessor(professorId);
            ctx.json(students);
        });

        app.post("/api/professors/{id}/grades", ctx -> {
            int professorId = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            int studentId = JsonUtil.intVal(body, "studentId");
            int courseId = JsonUtil.intVal(body, "courseId");
            String term = JsonUtil.strOrNull(body, "term");
            if (term == null) term = CURRENT_TERM;
            float grade = JsonUtil.floatVal(body, "grade");
            if (grade < 0.0f || grade > 4.0f) {
                throw new IllegalArgumentException("grade must be between 0.0 and 4.0");
            }
            int statusInput = JsonUtil.intVal(body, "status");
            if (statusInput < 1 || statusInput > 4) {
                throw new IllegalArgumentException("status must be 1 (Enrolled), 2 (Completed), 3 (Failed), or 4 (Dropped)");
            }
            EnrollmentStatus status = EnrollmentStatus.fromDbValue(statusInput);

            enrollmentService.updateGrade(professorId, studentId, courseId, term, grade, status);
            ctx.json(Map.of("message", "Grade updated."));
        });
    }
}