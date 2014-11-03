package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TaskGrid extends GridPane {

	private static final int FIRST_COLUMN = 0;
	private static final int SECOND_COLUMN = 1;
	private static final int THIRD_COLUMN = 2;

	private static final int FIRST_ROW = 0;
	private static final int SECOND_ROW = 1;
	private static final int THIRD_ROW = 2;

	private static final int COLUMN_SPAN = 5;
	private static final int ROW_SPAN = 1;
	private static final int DESCRIPTION_WRAPPING_WIDTH = 350;

	private static final String PRIORITY_ICON_IMAGE_PATH = "/images/priority.png";
	private static final String OVERLAP_ICON_IMAGE_PATH = "/images/overlap.png";
	private static final String OVERDUE_ICON_IMAGE_PATH = "/images/overdue.png";
	private static final String DONE_ICON_IMAGE_PATH = "/images/done.png";
	private static final String BLOCKING_ICON_IMAGE_PATH = "/images/blocking.png";

	private static final String ALTERNATE_TIMING_TEXT = "Alternate timing(s): \n";

	private int id;
	Label idContainer;

	public TaskGrid(int id, Task task) {
		this.id = id;
		configureTaskGrid();
		displayID(id, task);
		displayTime(task);
		displayTaskDescription(task);
		checkAndDisplayTaskIcon(task);
	}

	private void configureTaskGrid() {
		this.setPrefWidth(460);
		this.setHgap(10);
		this.setVgap(5);
		this.setPadding(new Insets(5));
		this.getStyleClass().add("grid");
		//this.setGridLinesVisible(true);
	}

	private void displayID(int id, Task task) {
		idContainer = new Label(Integer.toString(id + 1));
		idContainer.getStyleClass().add("idLabel");
		this.add(idContainer, FIRST_COLUMN, FIRST_ROW);
	}

	public void highlight() {
		idContainer.getStyleClass().add("isSelected");
	}

	private String getTimeFormat(Date date) {
		return new SimpleDateFormat("h:mm a").format(date);
	}

	private String getDateTimeFormat(Date date) {
		return new SimpleDateFormat("dd MMM h:mm a").format(date);
	}

	private String getDateFormat(Date date) {
		return new SimpleDateFormat("dd MMM").format(date);
	}

	private String getAllDayTimeFormat(Date date) {
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}

	private void displayTime(Task task) {
		String startTime, endTime, nextTiming, lastTiming;
		String alternateTiming = ALTERNATE_TIMING_TEXT;
		String allDayTimeFormat = "00:00:01";

		Date startDate = task.getDateStart();
		Date endDate = task.getDateEnd();
		Date nextDate = task.getNextDate();

		List<Date> allDate = task.getAllDates();

		// For displaying task that is eg. 5pm or 6pm or 7pm
		if (task.isBlocking()) {
			if (nextDate != null) {
				nextTiming = getTimeFormat(nextDate);
				Label nextTimeLabel = new Label(nextTiming);

				for (int i = 0; i < allDate.size(); i++) {
					if (allDate.get(i).after(nextDate)) {
						alternateTiming += getDateTimeFormat(allDate.get(i));
						if (i != allDate.size() - 1) {
							alternateTiming += ", ";
						}
					}
				}
				if (!alternateTiming.equals(ALTERNATE_TIMING_TEXT)) {
					Text text = new Text(10, 20, alternateTiming);
					text.setFont(Font.font("System", FontWeight.BOLD, 12));
					text.setWrappingWidth(DESCRIPTION_WRAPPING_WIDTH - 50);
					this.add(text, THIRD_COLUMN, SECOND_ROW);
				}

				this.add(nextTimeLabel, SECOND_COLUMN, FIRST_ROW);

			} else {
				// if there's no next date, get the last date to be displayed
				lastTiming = getTimeFormat(endDate);
				Label lastTimingLabel = new Label(lastTiming);
				this.add(lastTimingLabel, SECOND_COLUMN, FIRST_ROW);
			}
		} else if (task.isRange()) {
			startTime = getTimeFormat(startDate);
			if (TaskCatalystCommons.isSameDate(startDate, endDate)) {
				endTime = getTimeFormat(endDate);
			} else {
				endTime = getTimeFormat(endDate) + "\n("
						+ getDateFormat(endDate) + ")";
			}

			Text startTimeLabel = new Text(startTime + "\nto");
			Text endTimeLabel = new Text(endTime);
			
			this.add(startTimeLabel, SECOND_COLUMN, FIRST_ROW);
			this.add(endTimeLabel, SECOND_COLUMN, SECOND_ROW);
		} else {
			if (startDate != null) {
				String checkAllDay = getAllDayTimeFormat(startDate);
				if (checkAllDay.equals(allDayTimeFormat)) {
					Label startTimeLabel = new Label("All Day");
					this.add(startTimeLabel, SECOND_COLUMN, FIRST_ROW);
				} else {
					startTime = getTimeFormat(startDate);
					Label startTimeLabel = new Label(startTime);
					this.add(startTimeLabel, SECOND_COLUMN, FIRST_ROW);
				}
			}
		}
	}

	private void displayTaskDescription(Task task) {
		Text description = new Text(task.getDescription());
		description.setWrappingWidth(DESCRIPTION_WRAPPING_WIDTH);
		this.add(description, THIRD_COLUMN, FIRST_ROW);
		this.setPrefHeight(0);
	}

	private void checkAndDisplayTaskIcon(Task task) {
		int iconColumn, iconRow;
		HBox iconContainer = new HBox();

		iconColumn = THIRD_COLUMN;

		if (task.isBlocking()) {
			iconRow = THIRD_ROW;
			iconContainer = createIconWithText(iconContainer,
					BLOCKING_ICON_IMAGE_PATH, "Reserved");
		} else {
			iconRow = SECOND_ROW;
		}

		if (task.isDone()) {
			iconContainer = createIconWithText(iconContainer,
					DONE_ICON_IMAGE_PATH, "Done");
		}
		if (task.isPriority()) {
			iconContainer = createIconWithText(iconContainer,
					PRIORITY_ICON_IMAGE_PATH, "Priority");
		}
		if (task.isOverdue()) {
			iconContainer = createIconWithText(iconContainer,
					OVERDUE_ICON_IMAGE_PATH, "Overdue");
		}
		if (task.isOverlapping()) {
			iconContainer = createIconWithText(iconContainer,
					OVERLAP_ICON_IMAGE_PATH, "Overlapping");
		}
		iconContainer.getStyleClass().add("iconLabelStyle");
		this.add(iconContainer, THIRD_COLUMN, iconRow);
	}

	private HBox createIconWithText(HBox container, String imagePath,
			String labelText) {
		Image image = new Image(imagePath);
		Label iconLabel = new Label(labelText);
		ImageView icon = new ImageView();

		icon.setImage(image);
		container.getChildren().add(icon);
		container.getChildren().add(iconLabel);

		return container;
	}

	public int getTaskGridID() {
		return id;
	}

}
