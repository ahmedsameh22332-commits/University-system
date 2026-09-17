package Api;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

import java.sql.SQLException;
import java.util.Map;

public class ApiServer {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> it.anyHost());
            });
        });

        app.exception(IllegalArgumentException.class, (e, ctx) ->
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", e.getMessage())));

        app.exception(IllegalStateException.class, (e, ctx) ->
                ctx.status(HttpStatus.CONFLICT).json(Map.of("error", e.getMessage())));

        app.exception(NumberFormatException.class, (e, ctx) ->
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", "invalid number in request")));

        app.exception(SQLException.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("error", "database error"));
        });

        AuthRoutes.register(app);
        CourseRoutes.register(app);
        StudentRoutes.register(app);
        ProfessorRoutes.register(app);
        AdminRoutes.register(app);

        app.get("/", ctx -> ctx.html(
                "<h2>Student Management System API</h2>" +
                        "<p>Click a link below to test a GET endpoint directly:</p>" +
                        "<ul>" +
                        "<li><a href='/api/courses'>GET /api/courses</a></li>" +
                        "<li><a href='/api/admin/users'>GET /api/admin/users</a></li>" +
                        "</ul>"
        ));

        app.start(7070);
        System.out.println("REST API running on http://localhost:7070");
    }
}