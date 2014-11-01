package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class TaskGrid extends GridPane {
	// Image location in DarkTheme.css
	@FXML
	ImageView priorityIcon;
	@FXML
	ImageView overlapIcon;

	private static LogicActual logic;

	private static final int FIRST_COLUMN = 0;
	private static final int SECOND_COLUMN = 1;
	private static final int THIRD_COLUMN = 2;

	private static final int FIRST_ROW = 0;
	private static final int SECOND_ROW = 1;
	
	private static final String PRIORITY_IMAGE_PATH = "/images/priority.png";
	private static final String OVERLAP_IMAGE_PATH = "/images/overlap.png";

	public TaskGrid(int id, Task task) {
		configureTaskGrid();
		displayID(id);
		displayTime(task);
		displayTaskDescription(task);
		checkAndDisplayTaskIcon(task);
	}
	
	private void configureTaskGrid() {
		this.setPrefWidth(460);
		this.setHgap(10);
		this.setVgap(5);
		this.getStyleClass().add("grid");
		// this.setGridLinesVisible(true);
	}
	
	private void displayID(int id) {
		Label idContainer = new Label(Integer.toString(id + 1));
		idContainer.getStyleClass().add("idLabel");
		this.add(idContainer, FIRST_COLUMN, FIRST_ROW);
	}

	private void displayTime(Task task) {
		if(task.getDateStart() != null){
			Date startDate = task.getDateStart();
			Date endDate = task.getDateEnd();
			
			String startTime = new SimpleDateFormat("h:mm a").format(startDate);
			String endTime = new SimpleDateFormat("h:mm a").format(endDate);
			
			Label startTimeLabel= new Label(startTime);
			Label endTimeLabel = new Label(endTime);

			this.add(startTimeLabel, SECOND_COLUMN, FIRST_ROW);
			this.add(endTimeLabel, SECOND_COLUMN, SECOND_ROW);
		}else{
			Label startTimeLabel= new Label("All Day");
			this.add(startTimeLabel, SECOND_COLUMN, FIRST_ROW);
		}
		
	}

	private void displayTaskDescription(Task task) {
		Label description = new Label(task.getDescription());
		description.setWrapText(true);
		
		this.add(description, THIRD_COLUMN, FIRST_ROW);
	}

	private void checkAndDisplayTaskIcon(Task task) {
		// load the image
        Image priorityImage = new Image(PRIORITY_IMAGE_PATH);
        Image overlapImage = new Image(OVERLAP_IMAGE_PATH);
        
        ImageView priorityIcon = new ImageView();
        priorityIcon.setImage(priorityImage);

        ImageView overlapIcon = new ImageView();
        overlapIcon.setImage(overlapImage);

		switch (task.getHighlightType()) {
			case LAST_ADDED:
				break;
			case PRIORITY:
				this.add(priorityIcon, THIRD_COLUMN, SECOND_ROW);
				break;
			case OVERLAP:
				this.add(overlapIcon, THIRD_COLUMN, SECOND_ROW);
				break;
			case PRIORITY_OVERLAP:
				break;
			default:
				break;
		}
	}
}
