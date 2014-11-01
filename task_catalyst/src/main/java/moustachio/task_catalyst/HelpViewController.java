package moustachio.task_catalyst;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HelpViewController {
	@FXML
	private TabPane helpPane;
	
	@FXML
	private Button exitButton;
	
	private Stage helpStage;
	
	public HelpViewController () {
		
	}
	
	public void setHelpStage(Stage helpStage) {
		this.helpStage = helpStage;
	}
	
	 @FXML
	 public void initialize() {
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
	 
	 public void openHelpWindow(){
		 Pane helpPane;
		 try{
			 helpPane = FXMLLoader.load(getClass().getResource("/fxml/quickGuide.fxml"));
			 this.helpStage.setScene(new Scene(helpPane));
			 this.helpStage.initStyle(StageStyle.UNDECORATED);
			 this.helpStage.showAndWait();

		 } catch(IOException e){
			 e.printStackTrace();
		 }
	 }
}
