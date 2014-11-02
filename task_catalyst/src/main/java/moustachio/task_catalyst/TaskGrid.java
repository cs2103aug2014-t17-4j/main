package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class TaskGrid extends GridPane {

	private static LogicActual logic;

	private static final int FIRST_COLUMN = 0;
	private static final int SECOND_COLUMN = 1;
	private static final int THIRD_COLUMN = 2;

	private static final int FIRST_ROW = 0;
	private static final int SECOND_ROW = 1;
	private static final int THIRD_ROW = 2;

	private static final int COLUMN_SPAN = 5;
	private static final int ROW_SPAN = 1;

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
		ColumnConstraints idColumn = new ColumnConstraints();
		ColumnConstraints timeColumn = new ColumnConstraints();

		// idColumn.setPercentWidth(10);
		// timeColumn.setPercentWidth(17);
		this.setPrefWidth(460);
		//this.setPrefHeight(value)
		this.setHgap(10);
		this.setVgap(5);
		this.setPadding(new Insets(5));

		this.getStyleClass().add("grid");
		// this.getColumnConstraints().addAll(idColumn, timeColumn);
		this.setGridLinesVisible(true);
	}

	private void displayID(int id) {
		Label idContainer = new Label(Integer.toString(id + 1));
		idContainer.getStyleClass().add("idLabel");
		this.add(idContainer, FIRST_COLUMN, FIRST_ROW);
	}

	private String getTimeFormat(Date date) {
		return new SimpleDateFormat("h:mm a").format(date);
	}

	private String getAllDayFormat(Date date) {
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}

	private void displayTime(Task task) {
		String startTime, endTime, nextTiming, lastTiming;
		String alternateTiming = "Alternate timing(s): ";
		String allDay = "00:00:01";

		Date startDate = task.getDateStart();
		Date endDate = task.getDateEnd();
		Date nextDate = task.getNextDate();

		List<Date> allDate = task.getAllDates();

		// For displaying task that is eg. 5pm or 6pm or 7pm
		if (task.isBlocking()) {
			if (task.getNextDate() != null) {
				nextTiming = getTimeFormat(nextDate);
				Label nextTimeLabel = new Label(nextTiming);
				nextTimeLabel.getStyleClass().add("withborder");
				this.add(nextTimeLabel, SECOND_COLUMN, FIRST_ROW);
				for (int i = 0; i < allDate.size(); i++) {
					if (allDate.get(i).after(nextDate)) {
						alternateTiming += getTimeFormat(allDate.get(i)) + " ";
					}
				}
				this.add(new Label(alternateTiming), THIRD_COLUMN, SECOND_ROW);
			} else {
				// if there's no next date, get the last date to be displayed
				lastTiming = getTimeFormat(endDate);
				Label lastTimingLabel = new Label(lastTiming);
				lastTimingLabel.getStyleClass().add("withborder");
				this.add(lastTimingLabel, SECOND_COLUMN, FIRST_ROW);
			}
		} else {
			if (task.getNextDate() != null) {
				startTime = getTimeFormat(startDate);
				Label startTimeLabel = new Label(startTime);
				if (task.isRange()) {
					endTime = getTimeFormat(endDate);
					Label endTimeLabel = new Label(endTime);
					endTimeLabel.getStyleClass().add("withborder");
					this.add(endTimeLabel, SECOND_COLUMN, SECOND_ROW);
				}
				this.add(startTimeLabel, SECOND_COLUMN, FIRST_ROW);
			} else if (task.getNextDate() == null && task.getDateEnd() != null) {
				lastTiming = getTimeFormat(endDate);
				Label lastTimingLabel = new Label(lastTiming);
				lastTimingLabel.getStyleClass().add("withborder");
				this.add(lastTimingLabel, SECOND_COLUMN, FIRST_ROW);
			} else {
				Label startTimeLabel = new Label("All Day");
				startTimeLabel.getStyleClass().add("withborder");
				this.add(startTimeLabel, SECOND_COLUMN, FIRST_ROW);
			}
		}
	}

	private void displayTaskDescription(Task task) {
		javafx.scene.text.Text description = new javafx.scene.text.Text(task.getDescription());
		//description.setWrapText(true);
		description.setWrappingWidth(400);
		description.getStyleClass().add("withborder");
		this.add(description, THIRD_COLUMN, FIRST_ROW, COLUMN_SPAN, ROW_SPAN);
		this.setPrefHeight(0);
	}

	private void checkAndDisplayTaskIcon(Task task) {
		ImageView icon = new ImageView();
		int iconColumn, iconRow;

		iconColumn = SECOND_COLUMN;

		if (task.isBlocking()) {
			iconRow = THIRD_ROW;
		} else {
			iconRow = SECOND_ROW;
		}

		if (task.isDone()) {
			Image doneImage = new Image(DONE_ICON_IMAGE_PATH);
			iconColumn++;
			icon.setImage(doneImage);
			this.add(icon, iconColumn, iconRow);
		}
		if (task.isPriority()) {
			Image priorityImage = new Image(PRIORITY_ICON_IMAGE_PATH);
			iconColumn++;
			ImageView priorityIcon = new ImageView();
			priorityIcon.setImage(priorityImage);
			this.add(priorityIcon, iconColumn, iconRow);
		}
		if (task.isOverdue()) {
			iconColumn++;
			Image overdueImage = new Image(OVERDUE_ICON_IMAGE_PATH);
			ImageView overdueIcon = new ImageView();
			overdueIcon.setImage(overdueImage);
			this.add(overdueIcon, iconColumn, iconRow);
		}
		if (task.isOverlapping()) {
			iconColumn++;
			Image overlapImage = new Image(OVERLAP_ICON_IMAGE_PATH);
			ImageView overlapIcon = new ImageView();
			overlapIcon.setImage(overlapImage);
			this.add(overlapIcon, iconColumn, iconRow);
		}
	}
}
