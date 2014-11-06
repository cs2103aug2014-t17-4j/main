package moustachio.task_catalyst;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This program is to create pop-up window for comprehensive quick guide for users. 
 * 
 * Users can use hotkey "Ctrl+H" to launch quick guide window and use "Ctrl+H" to close 
 * Task Catalyst while it is running.
 * 
 * @author A0112764J
 *
 */

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
	
	private double initialY;
	private double initialX;

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
	
	private void addDragListeners(final Node helpUI) {
		helpUI.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if (me.getButton() != MouseButton.MIDDLE) {
					initialX = me.getSceneX();
					initialY = me.getSceneY();
				} else {
					helpUI.getScene().getWindow().centerOnScreen();
					initialX = helpUI.getScene().getWindow().getX();
					initialY = helpUI.getScene().getWindow().getY();
				}
			}
		});

		helpUI.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if (me.getButton() != MouseButton.MIDDLE) {
					helpUI.getScene().getWindow()
					.setX(me.getScreenX() - initialX);
					helpUI.getScene().getWindow()
					.setY(me.getScreenY() - initialY);
				}
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
				addDragListeners(helpPane);
				controller.setStage(helpStage);
				helpStage.setScene(new Scene(helpPane));
				helpStage.initStyle(StageStyle.UNDECORATED);
				helpStage.setX(0);
				helpStage.setY(30);
				helpStage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}