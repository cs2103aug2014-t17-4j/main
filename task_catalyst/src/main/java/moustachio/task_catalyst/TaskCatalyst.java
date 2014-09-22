package moustachio.task_catalyst;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TaskCatalyst extends Application {
	private double initialY;
	private double initialX;
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		Parent root = FXMLLoader.load(getClass().getResource("interface.fxml"));
		Scene scene = new Scene(root);
		addDragListeners(root);
		// set stylesheet
		scene.getStylesheets().add(
				getClass().getResource("DarkTheme.css").toExternalForm());

		// set stage
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	private void addDragListeners(final Node n) {

		n.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {

				if (me.getButton() != MouseButton.MIDDLE) {
					initialX = me.getSceneX();
					initialY = me.getSceneY();
				} else {
					n.getScene().getWindow().centerOnScreen();
					initialX = n.getScene().getWindow().getX();
					initialY = n.getScene().getWindow().getY();
				}

			}
		});

		n.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if (me.getButton() != MouseButton.MIDDLE) {
					n.getScene().getWindow().setX(me.getScreenX() - initialX);
					n.getScene().getWindow().setY(me.getScreenY() - initialY);
				}
			}
		});
	}
}
