package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private Connection conexion;
    private static final String url = "jdbc:mysql://localhost/bdminimarket";
    private static final String usuario = "root";
    private static final String clave = "";

    public Connection getConnection() throws SQLException {
        try {
            conexion = DriverManager.getConnection(url, usuario, clave);
            System.out.println("Conexion correcta");
        } catch (SQLException e) {
            System.out.println("Error en la conexion");
        }
        return conexion;
    }
}
