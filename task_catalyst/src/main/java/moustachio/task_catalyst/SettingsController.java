package moustachio.task_catalyst;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

/**
 * This Class is not implemented
 * @author Toh Zhen Yu (A0111921W)
 */

public class SettingsController {
	@FXML
	private BorderPane rootBorderPane;
	@FXML
	private Button exitButton;
	@FXML
	private Button saveButton;
	@FXML
	private Button cancelButton;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		testInterface();
	}

	private void testInterface() {
		assert rootBorderPane != null : "fx:id=\"rootBorderPane\" was not injected: check your FXML file 'settings.fxml'.";
		assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'settings.fxml'.";
		assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'settings.fxml'.";
		assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'settings.fxml'.";
	}
}
