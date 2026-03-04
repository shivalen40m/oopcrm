package Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDB {

    public static void main(String[] args) {

        try {
            Connection conn =
                    DriverManager.getConnection(
                            "jdbc:postgresql://localhost:5432/dms_db",
                            "postgres",
                            "password");

            System.out.println("Database Connected Successfully!");

        } catch (Exception e) {
            System.out.println("Error reading database");
            e.printStackTrace();
        }
    }

}
