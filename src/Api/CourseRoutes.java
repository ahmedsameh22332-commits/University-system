package Api;

import DAO.CourseDAO;
import model.Course;
import io.javalin.Javalin;

import java.util.List;
import java.util.Map;

public class CourseRoutes {
    private static final CourseDAO courseDAO = new CourseDAO();

    public static void register(Javalin app) {
        app.get("/api/courses", ctx -> {
            List<Course> courses = courseDAO.findAll();
            ctx.json(courses);
        });

        app.get("/api/courses/{id}", ctx -> {
            int courseId = Integer.parseInt(ctx.pathParam("id"));
            Course c = courseDAO.findById(courseId);
            if (c == null) {
                ctx.status(404).json(Map.of("error", "no such course"));
                return;
            }
            ctx.json(c);
        });
    }
}