package app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
//    private static final String URL = "jdbc:mariadb://localhost:3306/atm";
//    private static final String USER = "root";
//    private static final String PASSWORD = "";

    private static final String URL = "jdbc:postgresql://localhost:5432/ATM";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER,PASSWORD);
    }
}
