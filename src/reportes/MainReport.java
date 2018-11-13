package reportes;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainReport extends Application {
	public void start(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/reportes/interfaz.fxml"));
		try {
			Parent parent = (Parent) loader.load();
			Scene scene = new Scene(parent);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Salones");
			ControladorInterfaz controller = (ControladorInterfaz) loader.getController();
			controller.prepararPantalla();
			primaryStage.show();
		} catch (IOException e) {
			Logger.getLogger(MainReport.class.getName()).log(Level.SEVERE, "No se ha podido cargar el archivo la vista",
					e);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
