package moustachio.task_catalyst;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class TaskGrid extends GridPane {
	// Image location in DarkTheme.css
	@FXML
	ImageView priorityIcon;
	@FXML
	ImageView overlapIcon;

	private static LogicActual logic;

	private static final int FIRST_COLUMN = 0;
	private static final int SECOND_COLUMN = 1;
	private static final int THIRD_COLUMN = 1;
	
	private static final int FIRST_ROW = 0;
	private static final int SECOND_ROW = 1;

	public TaskGrid(int id, Task task) {
		// super ();
		this.setPrefWidth(460);
		this.setHgap(10);
		this.setVgap(5);
		this.getStyleClass().add("grid");
		// this.setGridLinesVisible(true);

		Label startTime = new Label("12PM");
		Text endTime = new Text("1PM");

		this.add(startTime, FIRST_COLUMN, FIRST_ROW);
		this.add(endTime, FIRST_COLUMN, SECOND_ROW);

		Label description = new Label(task.getDescription());
		description.setWrapText(true);

		ImageView icon;

		switch (task.getHighlightType()) {
		case LAST_ADDED:
			icon = null;
			break;
		case PRIORITY:
			icon = priorityIcon;
			this.add(icon, SECOND_COLUMN, SECOND_ROW);
			break;
		case OVERLAP:
			icon = overlapIcon;
			this.add(icon, SECOND_COLUMN, SECOND_ROW);
			break;
		case PRIORITY_OVERLAP:
			icon = null;
			break;
		default:
			icon = null;
			break;
		}

		this.add(description, SECOND_COLUMN, FIRST_ROW);
		//this.add(icon, SECOND_COLUMN, SECOND_ROW);
	}

}
