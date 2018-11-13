package reportes;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public abstract class AbstractJasperReports {
	private static JasperReport report;
	private static JasperPrint reportFilled;
	private static JasperViewer viewer;

	public static void createReport(Connection conn, InputStream jasperStream, Map<String, Object> parameters)
			throws IOException, JRException {
		if (jasperStream == null) {
			throw new IOException("No se encuentra el archivo .jasper");
		}
		report = (JasperReport) JRLoader.loadObject(jasperStream);
		reportFilled = JasperFillManager.fillReport(report, parameters, conn);
	}

	public static void showViewer() {
		viewer = new JasperViewer(reportFilled, false);
		viewer.setVisible(true);
	}

	public static void exportToPDF(String destination) throws JRException {
		try {
			JasperExportManager.exportReportToPdfFile(reportFilled, destination);
		} catch (JRException ex) {
			Logger.getLogger(AbstractJasperReports.class.getName()).log(Level.SEVERE,
					"No se pudo exportar a pdf. ¿Problemas de permisos?", ex);
			throw ex;
		}
	}
}

/*
 * Location:
 * D:\JavaProyects\jar\salaconferencia.jar!\reportes\AbstractJasperReports.class
 * Java compiler version: 8 (52.0) JD-Core Version: 0.7.1
 */