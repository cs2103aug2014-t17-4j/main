package moustachio.task_catalyst;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HelpViewController {
	@FXML
	private TabPane helpPane;
	@FXML
	private Tab hashtagTab;
	@FXML
	private Tab basicFeaturesTab;
	@FXML
	private Tab specialFeaturesTab;
	@FXML
	private Tab hotKeysTab;
	@FXML
	private Button exitButton;

	private Stage helpStage;

	private static final String QUICKGUIDE_FXML_PATH = "/fxml/quickGuide.fxml";

	public HelpViewController() {

	}

	public void setHelpStage(Stage helpStage) {
		this.helpStage = helpStage;
	}

	@FXML
	public void initialize() {

		assert helpPane != null : "fx:id=\"helpPane\" was not injected: check your FXML file 'interface.fxml'.";
		assert hashtagTab != null : "fx:id=\"hashtagTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert basicFeaturesTab != null : "fx:id=\"basicFeaturesTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert specialFeaturesTab != null : "fx:id=\"specialFeaturesTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert hotKeysTab != null : "fx:id=\"hotKeysTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'interface.fxml'.";

		exitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						System.exit(0);
					}
				});
			}
		});
	}

	public void openHelpWindow() {
		Pane helpPane;
		try {
			helpPane = FXMLLoader.load(getClass().getResource(
					QUICKGUIDE_FXML_PATH));
			Stage stage = new Stage();
			//this.helpStage.setScene(new Scene(helpPane));
			//this.helpStage.initStyle(StageStyle.UNDECORATED);
			//this.helpStage.showAndWait();
			stage.setScene(new Scene(helpPane));
			stage.initStyle(StageStyle.UNDECORATED);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
