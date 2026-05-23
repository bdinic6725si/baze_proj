import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/astronomija";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Baza2026!";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Konekcija uspesna!");
            } catch (SQLException e) {
                System.out.println("Greška: " + e.getMessage());
            }
        }
        return connection;
    }
}