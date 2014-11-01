package moustachio.task_catalyst;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class TaskGrid extends GridPane{
	@FXML
	ImageView priorityIcon;
	@FXML
	ImageView overlapIcon;
	private static LogicActual logic;
	
	private static final int FIRST_COLUMN = 0;
	private static final int FIRST_ROW = 0;
	private static final int SECOND_COLUMN = 1;
	private static final int SECOND_ROW = 1;

	
	public TaskGrid(Task task){
		//super ();
		this.setPrefWidth(460);
		this.setHgap(10);
		this.setVgap(5);
		this.getStyleClass().add("grid");
		//this.setGridLinesVisible(true);
		
		Label startTime = new Label("12PM");
		Text endTime = new Text("1PM"); 
		
		this.add(startTime, FIRST_COLUMN, FIRST_ROW);
		this.add(endTime, FIRST_COLUMN, SECOND_ROW);
		
		Label description = new Label(task.getDescription()); 
		description.setWrapText(true);
		Text taskType = new Text("PRIORITY"); 
		this.add(description, SECOND_COLUMN, FIRST_ROW);
		
		this.add(taskType, SECOND_COLUMN, SECOND_ROW);
	}
	
}
