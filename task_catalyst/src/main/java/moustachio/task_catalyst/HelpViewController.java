package moustachio.task_catalyst;


import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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
	@FXML
	private TextArea hashTagArea;
	@FXML
	private ImageView basicFeatures;
	@FXML
	private ImageView specialFeatures;
	@FXML
	private ImageView hashtags;
	@FXML
	private ImageView hotkeys;

	private Stage helpStage;

	private static final String QUICKGUIDE_FXML_PATH = "/fxml/quickGuide.fxml";
	
	private HelpViewController controller;

	public HelpViewController() {
		
	}

	public void setStage(Stage helpStage) {
		this.helpStage = helpStage;
	}
	
	Stage getStage() {
		return this.helpStage;
	}

	@FXML
	public void initialize() {

		assert helpPane != null : "fx:id=\"helpPane\" was not injected: check your FXML file 'interface.fxml'.";
		assert hashtagTab != null : "fx:id=\"hashtagTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert basicFeaturesTab != null : "fx:id=\"basicFeaturesTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert specialFeaturesTab != null : "fx:id=\"specialFeaturesTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert hotKeysTab != null : "fx:id=\"hotKeysTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'interface.fxml'.";
	}
	
	@FXML
	public void handleExit() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				helpStage.hide();
			}
		});
	}
	
	public void openHelpWindow() {
		AnchorPane helpPane;
		if (helpStage != null) {
			helpStage.show();
			helpStage.toFront();
		}
		else {
			try {
				helpStage = new Stage();
				FXMLLoader loader = new FXMLLoader(getClass().getResource(
						QUICKGUIDE_FXML_PATH));
				helpPane = loader.load();
				
				controller = loader.getController();
				controller.setStage(helpStage);
				helpStage.setScene(new Scene(helpPane));
				helpStage.initStyle(StageStyle.UNDECORATED);
				helpStage.setX(0);
				helpStage.setY(30);
				//if(helpStage.)
				helpStage.showAndWait();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
