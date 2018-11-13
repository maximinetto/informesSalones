package reportes;

import helper.Configuracion;
import helper.ParseLocalDate;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;

import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.IntegerStringConverter;
import javax.imageio.ImageIO;
import net.sf.jasperreports.engine.JRException;
import reportes.AbstractJasperReports;
import reportes.Conexion;

public class ControladorInterfaz implements Initializable {
	private Map<String, Object> parameters;
	@FXML
	private TextField txtIP;
	@FXML
	private TextField txtPuerto;
	@FXML
	private Label lblIP;
	@FXML
	private Label lblPuerto;
	@FXML
	private CheckBox chkDefecto;
	@FXML
	private DatePicker dtFechaInicio;
	@FXML
	private DatePicker dtFechaFin;
	@FXML
	private Button btnTest;
	@FXML
	private Button btnGenerarReporte;
	@FXML
	private Button btnExportarPDF;
	@FXML
	private Button btnSalir;

	public void initialize(URL arg0, ResourceBundle arg1) {
		this.dtFechaInicio.setDisable(true);
		this.dtFechaFin.setDisable(true);
	}

	public void prepararPantalla() {
		this.setTxtIP();
		this.setTxtPort();
		this.setCheckBox();
		this.setBtnTest();
		this.setBtnReportes();
		this.setBtnExportPDF();
		this.salirPrograma();
	}

	private void salirPrograma() {
		this.btnSalir.setOnAction(event -> {
			Stage stage = (Stage) this.btnSalir.getScene().getWindow();
			stage.close();
		});
	}

	private void setBtnTest() {
		this.btnTest.setOnAction(event -> {
			String server = this.txtIP.getText();
			String port = this.txtPuerto.getText();
			if (!this.validateIP(server)) {
				this.mensajeAlerta(Alert.AlertType.WARNING, "Ip no válida",
						"La ip ingresada no es válida o no ingresó una ip");
			} else if (port.isEmpty()) {
				this.mensajeAlerta(Alert.AlertType.WARNING, "Campos vacios",
						"Coloque un puerto para el servidor MYSQL");
			} else {
				try {
					Conexion.conectar((String) server, (String) port);
					this.mensajeAlerta(Alert.AlertType.INFORMATION, "Conexión", "Conexion Exitosa");
					this.mostrarOtrosBotones();
				} catch (InstantiationException e) {
					this.mensajeAlerta(Alert.AlertType.ERROR, "No se encontró el driver",
							"El programa no puede funcionar sin el driver MYSQL\n" + e);
					Logger.getLogger(ControladorInterfaz.class.getName()).log(Level.SEVERE,
							"No se encontró el driver", e);
				} catch (IllegalAccessException e) {
					this.mensajeAlerta(Alert.AlertType.ERROR, "Acceso denegado",
							"No se puedo acceder al servidor\n" + e);
					Logger.getLogger(ControladorInterfaz.class.getName()).log(Level.SEVERE, "Acceso denegado", e);
				} catch (ClassNotFoundException e) {
					this.mensajeAlerta(Alert.AlertType.ERROR, "No se encontró el driver",
							"El programa no puede funcionar sin el driver MYSQL\n" + e);
					Logger.getLogger(ControladorInterfaz.class.getName()).log(Level.SEVERE,
							"No se encontró el driver", e);
				} catch (SQLException e) {
					this.mensajeAlerta(Alert.AlertType.ERROR, "Error en la conexión",
							"Error en la conexión al servidor\n¿Servidor, puerto incorrecto?" + e);
					Logger.getLogger(ControladorInterfaz.class.getName()).log(Level.SEVERE,
							"Error en la conexión ¿URL Incorrecta?", e);
				}
			}
		});
	}

	private void mostrarOtrosBotones() {
		this.btnGenerarReporte.setDisable(false);
		this.btnExportarPDF.setDisable(false);
		this.dtFechaInicio.setDisable(false);
		this.dtFechaFin.setDisable(false);
	}

	private final void setParameters(Date fechaInicio, java.util.Date fechaFin) {
		this.parameters = new HashMap<String, Object>();
		this.parameters.put("fechaInicio", fechaInicio);
		this.parameters.put("fechaFin", fechaFin);
		try {
			BufferedImage image = ImageIO.read(this.getClass().getResource("/imagenes/eemac.jpg"));
			this.parameters.put("logo", image);
		} catch (IOException e) {
			this.mensajeAlerta(Alert.AlertType.ERROR, "No se encontró el logo",
					"No se ha encontrado el logo de la EEMAC.\nEl programa terminará");
			this.salirPrograma();
		}
	}

	private void setTxtIP() {
		this.txtIP.setText(Configuracion.getIP());
	}

	private void setTxtPort() {
		this.txtPuerto.setText(Configuracion.getPuerto());
		ControladorInterfaz.integerTextField(this.txtPuerto);
	}

	private void setCheckBox() {
		this.chkDefecto.setOnAction(event -> {
			boolean isSelected = this.chkDefecto.isSelected();
			this.lblIP.setDisable(isSelected);
			this.txtIP.setDisable(isSelected);
			this.lblPuerto.setDisable(isSelected);
			this.txtPuerto.setDisable(isSelected);
			if (isSelected) {
				this.setTxtIP();
				this.setTxtPort();
			}
		});
	}

	private void setBtnReportes() {
		this.btnGenerarReporte.setOnAction(event -> {
			if (!this.verificarCamposErroneos()) {
				LocalDate fechaInicio = (LocalDate) this.dtFechaInicio.getValue();
				LocalDate fechaFin = (LocalDate) this.dtFechaFin.getValue();
				this.extraerFechas(fechaInicio, fechaFin);
				try {
					Connection connection = Conexion.getConnection();
					if (connection == null) {
						this.mensajeAlerta(Alert.AlertType.ERROR, "Error en la conexión",
								"No se ha establecido la conexión");
						return;
					}
					InputStream input = this.getClass().getResourceAsStream("/reportes/reporte.jasper");
					AbstractJasperReports.createReport(connection, input, this.parameters);
					AbstractJasperReports.showViewer();
				} catch (JRException e) {
					Logger.getLogger(ControladorInterfaz.class.getName()).log(Level.SEVERE, "Error", (Throwable) e);
					this.mensajeAlerta(Alert.AlertType.ERROR, "No se pudo cargar",
							"No se pudo cargar el reporte. Verifique las bibliotecas del JasperReport\n" + (Object) e);
				} catch (IOException e) {
					Logger.getLogger(ControladorInterfaz.class.getName()).log(Level.SEVERE, "Error", e);
					this.mensajeAlerta(Alert.AlertType.ERROR, "No se pudo cargar",
							"No se pudo cargar el reporte. El archivo template no existe\n" + e);
				}
				this.salvarPuertoIpConfiguracion();
			}
		});
	}

	private void extraerFechas(LocalDate fechaInicio, LocalDate fechaFin) {
		Date inicio = new Date(ParseLocalDate.convertToDate((LocalDate) fechaInicio).getTime());
		Date fin = new Date(ParseLocalDate.convertToDate((LocalDate) fechaFin).getTime());
		this.setParameters(inicio, fin);
	}

	private void salvarPuertoIpConfiguracion() {
		try {
			Configuracion.setIpPuerto((String) this.txtIP.getText(), (String) this.txtPuerto.getText());
		} catch (IOException e) {
			Logger.getLogger(ControladorInterfaz.class.getName()).log(Level.SEVERE,
					"Error al salvar configuración", e);
		}
	}

	private void setBtnExportPDF() {
		this.btnExportarPDF.setOnAction(event -> {
			if (this.verificarCamposErroneos()) {
				return;
			}
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)",
					new String[] { "*.pdf" });
			fileChooser.getExtensionFilters().add(extFilter);
			Stage primaryStage = (Stage) this.btnExportarPDF.getScene().getWindow();
			File file = fileChooser.showSaveDialog((Window) primaryStage);
			if (file != null) {
				try {
					Connection connection = Conexion.getConnection();
					if (connection == null) {
						this.mensajeAlerta(Alert.AlertType.ERROR, "Error en la conexión",
								"No se ha establecido la conexión");
						return;
					}
					LocalDate fechaInicio = (LocalDate) this.dtFechaInicio.getValue();
					LocalDate fechaFin = (LocalDate) this.dtFechaFin.getValue();
					this.extraerFechas(fechaInicio, fechaFin);
					InputStream input = this.getClass().getResourceAsStream("/reportes/reporte.jasper");
					AbstractJasperReports.createReport((Connection) connection, (InputStream) input, this.parameters);
					AbstractJasperReports.exportToPDF((String) file.getAbsolutePath());
					this.salvarPuertoIpConfiguracion();
					this.mensajeAlerta(Alert.AlertType.INFORMATION, "Guardado", "Se ha guardado correctamente el pdf");
				} catch (JRException e) {
					this.mensajeAlerta(Alert.AlertType.ERROR, "No se puedo exportar",
							"No se pudo exportar a pdf. ¿Problemas de permisos?");
				} catch (IOException e) {
					Logger.getLogger(ControladorInterfaz.class.getName()).log(Level.SEVERE, "Error", e);
					this.mensajeAlerta(Alert.AlertType.ERROR, "No se pudo cargar",
							"No se pudo cargar el reporte. El archivo template no existe\n" + e);
				}
			}
		});
	}

	public boolean validateIP(String ip) {
		String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	private static void integerTextField(TextField textField) {
		UnaryOperator<Change> integerFilter = change -> {
			String newText = change.getControlNewText();
			System.out.println("newText " + newText);
			if (newText.matches("-?([0-9]*)?")) {
				System.out.println("cumple regla");
				return change;
			}
			System.out.println("No cumple regla");
			return null;
		};
		try {
			textField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(),
					Integer.parseInt(textField.getText()), integerFilter));
		} catch (NumberFormatException ex) {
			textField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		}
	}

	private boolean verificarCamposErroneos() {
		if (this.dtFechaInicio.getValue() == null) {
			this.mensajeAlerta(Alert.AlertType.WARNING, "Campos vacios", "Coloque una fecha de inicio del reporte");
			return true;
		}
		if (this.dtFechaFin.getValue() == null) {
			this.mensajeAlerta(Alert.AlertType.WARNING, "Campos vacios", "Coloque una fecha final del reporte");
			return true;
		}
		if (this.dtFechaInicio.getValue().isAfter(this.dtFechaFin.getValue())) {
			this.mensajeAlerta(Alert.AlertType.WARNING, "Fechas inválidas",
					"La fecha de inicio no puede ser mayor que la fecha final del reporte");
			return true;
		}
		return false;
	}

	private void mensajeAlerta(Alert.AlertType alertType, String header, String context) {
		Alert alert = new Alert(alertType);
		if (alertType == Alert.AlertType.ERROR) {
			alert.setTitle("Error");
		} else if (alertType == Alert.AlertType.WARNING) {
			alert.setTitle("Atención");
		} else {
			alert.setTitle("Información");
		}
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.showAndWait();
	}
}