package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class TaskGrid extends GridPane {

	private static LogicActual logic;

	private static final int FIRST_COLUMN = 0;
	private static final int SECOND_COLUMN = 1;
	private static final int THIRD_COLUMN = 2;

	private static final int FIRST_ROW = 0;
	private static final int SECOND_ROW = 1;
	
	private static final String PRIORITY_ICON_IMAGE_PATH = "/images/priority.png";
	private static final String OVERLAP_ICON_IMAGE_PATH = "/images/overlap.png";
	private static final String OVERDUE_ICON_IMAGE_PATH = "/images/overdue.png";
	private static final String DONE_ICON_IMAGE_PATH = "/images/done.png";

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
		ColumnConstraints column1 = new ColumnConstraints();
	    column1.setPercentWidth(5);
	    ColumnConstraints column2 = new ColumnConstraints();
	    column2.setPercentWidth(17);
	    this.getColumnConstraints().addAll(column1, column2); // each get 50% of width
		//this.setGridLinesVisible(true);
	}
	
	private void displayID(int id) {
		Label idContainer = new Label(Integer.toString(id + 1));
		idContainer.getStyleClass().add("idLabel");
		this.add(idContainer, FIRST_COLUMN, FIRST_ROW);
	}

	private void displayTime(Task task) {
		if(task.getNextDate() != null){
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
		
		this.add(description, THIRD_COLUMN, FIRST_ROW , 5 , 1);
	}

	private void checkAndDisplayTaskIcon(Task task) {
		int iconColumn = SECOND_COLUMN;
		ImageView icon = new ImageView();
		
		if(task.isDone()){
			Image doneImage = new Image(DONE_ICON_IMAGE_PATH);
			iconColumn++;
			icon.setImage(doneImage);
			this.add(icon, iconColumn, SECOND_ROW);
		}
		if(task.isPriority()){
			Image priorityImage = new Image(PRIORITY_ICON_IMAGE_PATH);
			iconColumn++;
			ImageView priorityIcon = new ImageView();
			priorityIcon.setImage(priorityImage);
			this.add(priorityIcon, iconColumn, SECOND_ROW);
		}
		if(task.isOverdue()){
			iconColumn++;
			Image overdueImage = new Image(OVERDUE_ICON_IMAGE_PATH);
			ImageView overdueIcon = new ImageView();
			overdueIcon.setImage(overdueImage);
			this.add(overdueIcon, iconColumn, SECOND_ROW);
		}
		/*
        if(task.isOverlap()){
        	iconColumn++;
        	Image overlapImage = new Image(OVERLAP_ICON_IMAGE_PATH);
            ImageView overlapIcon = new ImageView();
            overlapIcon.setImage(overlapImage);
            this.add(overlapIcon, iconColumn, SECOND_ROW);
        }*/
	}
}
