package reportes;

import helper.Configuracion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexion {
	private static final String USER = Configuracion.getUsuario();
	private static final String PASS = Configuracion.getPassword();
	private static Connection connection;

	public static Connection conectar(String server, String port)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		String url = "jdbc:mysql://" + server + ":" + port + "/Reservas";
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		connection = DriverManager.getConnection(url, USER, PASS);
		return connection;
	}

	public static Connection getConnection() {
		return connection;
	}

	public static void cerrar() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, "No se pudo cerrar la conexión", e);
			}
		}
	}
}
