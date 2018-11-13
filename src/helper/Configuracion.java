package helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuracion {
	private static Properties properties;

	public static String getUsuario() {
		String user = null;
		if (properties == null)
			properties = new Properties();
		try {
			FileInputStream inputStream = new FileInputStream(".//conexion.properties");
			properties.load(inputStream);
			user = properties.getProperty("username");
			user = user.trim();
		} catch (FileNotFoundException e) {
			Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, "No se encuentra el archivo", e);
		} catch (IOException e) {
			Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, "Problema al abrir el archivo", e);
		}

		return user;
	}

	public static String getPassword() {
		String password = null;
		if (properties == null)
			properties = new Properties();
		try {
			InputStream inputStream = new FileInputStream(".//conexion.properties");
			properties.load(inputStream);
			password = properties.getProperty("password");
			password = password.trim();
		} catch (FileNotFoundException e) {
			Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, "No se encuentra el archivo", e);
		} catch (IOException e) {
			Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, "Problema al abrir el archivo", e);
		}

		return password;
	}

	public static String getIP() {
		String ip = null;
		if (properties == null) {
			properties = new Properties();
		}
		try {
			InputStream inputStream = new FileInputStream(".//conexion.properties");
			properties.load(inputStream);
			ip = properties.getProperty("ip");
			ip = ip.trim();
		} catch (FileNotFoundException e) {
			Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, "No se encuentra el archivo", e);
		} catch (IOException e) {
			Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, "Problema al abrir el archivo", e);
		}

		return ip;
	}

	public static String getPuerto() {
		String puerto = null;
		if (properties == null) {
			properties = new Properties();
		}
		try {
			InputStream inputStream = new FileInputStream(".//conexion.properties");
			properties.load(inputStream);
			puerto = properties.getProperty("port");
			puerto = puerto.trim();
		} catch (FileNotFoundException e) {
			Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, "No se encuentra el archivo", e);
		} catch (IOException e) {
			Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, "Problema al abrir el archivo", e);
		}

		return puerto;
	}

	public static void setIpPuerto(String ip, String puerto) throws IOException {

		if (properties == null)
			properties = new Properties();

		properties.setProperty("ip", ip);
		properties.setProperty("port", puerto);
		FileOutputStream os = null;
		try {
			os = new FileOutputStream("./conexion.properties");
			properties.store(os, "Guardado");
		} catch (IOException e) {
			Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, "Problema al abrir el archivo", e);
			throw e;
		} finally {
			if (os != null)
				os.close();
		}
	}
}