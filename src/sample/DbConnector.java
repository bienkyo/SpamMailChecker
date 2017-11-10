package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbConnector {
    private static final String url = "jdbc:mysql://localhost:3306/maildb";
    private static final String user = "root";
    private static final String password = "yahoo24";
    private static Connection connection;

    public static void initialize() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            Logger lgr = Logger.getLogger(DbConnector.class.getName());
            lgr.log(Level.WARNING, e.getMessage(), e);
            close();
        }

    }

    public static Connection getConnection() {
        return connection;
    }

    public static void close() {
        if (connection != null) {
            try {
                System.out.println("Closing connection");
                connection.close();
                System.out.println("Closed connection");
            } catch (SQLException e) {
                Logger lgr = Logger.getLogger(DbConnector.class.getName());
                lgr.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}
