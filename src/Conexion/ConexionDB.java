
package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL =
            "jdbc:mysql://localhost:3306/banco_academico_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USER = "banco_udb";
    private static final String PASSWORD = "BancoUDB2026!";

    public static Connection conectar() {

        Connection conexion = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            conexion = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Conexion exitosa a la base de datos.");

        } catch (ClassNotFoundException e) {

            System.err.println("No se encontro el Driver JDBC de MySQL.");
            e.printStackTrace();

        } catch (SQLException e) {

            System.err.println("Error de conexion a la base de datos.");
            e.printStackTrace();
        }

        return conexion;
    }
}
