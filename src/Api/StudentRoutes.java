package Api;

import DAO.StudentDAO;
import Service.AcademicPlanService;
import Service.EnrollmentService;
import model.Course;
import model.Enrollment;
import model.Student;
import io.javalin.Javalin;

import java.util.List;
import java.util.Map;

public class StudentRoutes {
    private static final StudentDAO studentDAO = new StudentDAO();
    private static final AcademicPlanService planService = new AcademicPlanService();
    private static final EnrollmentService enrollmentService = new EnrollmentService();
    private static final String CURRENT_TERM = "Fall2026"; // keep in sync with menu/MainMenu.java

    public static void register(Javalin app) {
        app.get("/api/students/{id}", ctx -> {
            int studentId = Integer.parseInt(ctx.pathParam("id"));
            Student s = studentDAO.findById(studentId);
            if (s == null) {
                ctx.status(404).json(Map.of("error", "no such student"));
                return;
            }
            ctx.json(s);
        });

        app.get("/api/students/{id}/academic-plan", ctx -> {
            int studentId = Integer.parseInt(ctx.pathParam("id"));
            List<Course> completed = planService.getCompletedCourses(studentId);
            List<Course> remaining = planService.getRemainingCourses(studentId);
            ctx.json(Map.of("completed", completed, "remaining", remaining));
        });

        app.get("/api/students/{id}/eligible-courses", ctx -> {
            int studentId = Integer.parseInt(ctx.pathParam("id"));
            List<Course> eligible = enrollmentService.getEligibleCourses(studentId);
            ctx.json(eligible);
        });

        app.get("/api/students/{id}/enrollments", ctx -> {
            int studentId = Integer.parseInt(ctx.pathParam("id"));
            List<Enrollment> history = enrollmentService.getEnrollmentHistory(studentId);
            ctx.json(history);
        });

        app.post("/api/students/{id}/enrollments", ctx -> {
            int studentId = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            int courseId = JsonUtil.intVal(body, "courseId");
            String term = JsonUtil.strOrNull(body, "term");
            if (term == null) term = CURRENT_TERM;

            enrollmentService.enroll(studentId, courseId, term);
            ctx.status(201).json(Map.of("message", "Enrollment successful."));
        });
    }
}