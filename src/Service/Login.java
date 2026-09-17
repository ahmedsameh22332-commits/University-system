package Service;

import DatabaseConnection.DBConnection;
import Status.Role;
import java.sql.*;
import java.util.Scanner;

public class Login {

    public Role authenticate(int id, String password) throws SQLException {
        String sql = "select role, password from users where id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("no user with that id");
                }

                String storedPassword = rs.getString("password");
                if (!storedPassword.equals(password)) {
                    throw new IllegalArgumentException("wrong password");
                }

                return Role.fromDbValue(rs.getInt("role"));
            }
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Login log = new Login();

        try {
            System.out.print("Enter id: ");
            int id = input.nextInt();
            input.nextLine();

            System.out.print("Enter password: ");
            String password = input.nextLine();

            Role role = log.authenticate(id, password);
            System.out.println("Login successful. Role: " + role);

        } catch (IllegalArgumentException e) {
            System.out.println("Login failed: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}