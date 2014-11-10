package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * TaskGrid creates Tasks using GridPane as a container
 */

//@author A0111921W
//credits:
//icon - http://www.iconsdb.com/icon-sets/web-2-orange-icons/
//Task table ideas from t17-3j
public class TaskGrid extends GridPane {

	private static final int COLUMN_FIRST = 0;
	private static final int COLUMN_SECOND = 1;
	private static final int COLUMN_THIRD = 2;

	private static final int ROW_FIRST = 0;
	private static final int ROW_SECOND = 1;
	private static final int ROW_THIRD = 2;

	private static final int WRAPPING_WIDTH_DESCRIPTION = 335;
	private static final int WRAPPING_WIDTH_ALT_TEXT = 300;

	private static final String PATH_PRIORITY_ICON_IMAGE = "/images/priority.png";
	private static final String PATH_OVERLAP_ICON_IMAGE = "/images/overlap.png";
	private static final String PATH_OVERDUE_ICON_IMAGE = "/images/overdue.png";
	private static final String PATH_DONE_ICON_IMAGE = "/images/done.png";
	private static final String PATH_BLOCKING_ICON_IMAGE = "/images/blocking.png";
	private static final String PATH_DEADLINE_ICON_IMAGE = "/images/deadline1.png";
	private static final String PATH_ERROR_ICON_IMAGE = "/images/error.png";
	
	private static final String LABEL_TEXT_PRIORITY = "Priority";
	private static final String LABEL_TEXT_OVERLAP = "Overlapping";
	private static final String LABEL_TEXT_OVERDUE = "Overdue";
	private static final String LABEL_TEXT_DONE = "Done";
	private static final String LABEL_TEXT_BLOCKING = "Reserved";
	private static final String LABEL_TEXT_DEADLINE = "Deadline";
	private static final String LABEL_TEXT_ERROR = "Error";
	
	private static final String TEXT_ALTERNATE_TIMING = "Alternate timing(s): \n";
	private static final String TEXT_ALL_DAY_TIME_FORMAT = "00:00:01";
	
	private int id;
	
	private Label idContainer;

	public TaskGrid(int id, Task task) {
		this.id = id;
		configureTaskGrid();
		displayID(id, task);
		displayTime(task);
		displayTaskDescription(task);
		checkAndDisplayTaskIcon(task);
	}

	private void configureTaskGrid() {
		ColumnConstraints idColumn = new ColumnConstraints();
		ColumnConstraints timeColumn = new ColumnConstraints();
		
		idColumn.setPercentWidth(8);
		timeColumn.setPercentWidth(15);
		
		this.getColumnConstraints().addAll(idColumn,timeColumn);
		
		this.setHgap(10);
		this.setVgap(5);
		this.setPadding(new Insets(5));
		this.getStyleClass().add("grid");
		//this.setGridLinesVisible(true);
	}

	private void displayID(int id, Task task) {
		idContainer = new Label(Integer.toString(id + 1));
		idContainer.getStyleClass().add("idLabel");
		this.setHalignment(idContainer, HPos.CENTER);
		this.add(idContainer, COLUMN_FIRST, ROW_FIRST);
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
		String alternateTiming = TEXT_ALTERNATE_TIMING;
		String allDayTimeFormat = TEXT_ALL_DAY_TIME_FORMAT;

		Date startDate = task.getDateStart();
		Date endDate = task.getDateEnd();
		Date nextDate = task.getNextDate();

		List<Date> allDate = task.getAllDates();

		// For displaying task that is eg. 5pm or 6pm or 7pm
		if (task.isBlocking()) {
			if (nextDate != null) {
				nextTiming = getTimeFormat(nextDate);

				for (int i = 0; i < allDate.size(); i++) {
					if (allDate.get(i).after(nextDate)) {
						alternateTiming += getDateTimeFormat(allDate.get(i));
						if (i != allDate.size() - 1) {
							alternateTiming += ", ";
						}
					}
				}
				
				if (!alternateTiming.equals(TEXT_ALTERNATE_TIMING)) {
					Text text = new Text(alternateTiming);
					text.setFont(Font.font("System", FontWeight.BOLD, 12));
					text.setWrappingWidth(WRAPPING_WIDTH_ALT_TEXT);
					this.add(text, COLUMN_THIRD, ROW_SECOND);
				}
				
				addStartTimeLabel(nextTiming);

			} else {
				// if there's no next date, get the last date to be displayed
				lastTiming = getTimeFormat(endDate);
				addStartTimeLabel(lastTiming);
			}
		} else if (task.isRange()) {
			startTime = getTimeFormat(startDate);
			
			if (TaskCatalystCommons.isSameDate(startDate, endDate)) {
				endTime = getTimeFormat(endDate);
			} else {
				endTime = getTimeFormat(endDate) + "\n("
						+ getDateFormat(endDate) + ")";
			}
			
			addStartTimeText(startTime + "\n     to");
			addEndTimeText(endTime);
		} else {
			if (startDate != null) {
				String checkAllDay = getAllDayTimeFormat(startDate);
				
				if (checkAllDay.equals(allDayTimeFormat)) {
					addStartTimeLabel("All Day");
				} else {
					startTime = getTimeFormat(startDate);
					addStartTimeLabel(startTime);
				}
			}else{
				addStartTimeLabel("--:--");
			}
		}
	}

	private void addStartTimeText(String text) {
		Text container = new Text(text);
		this.setHalignment(container, HPos.CENTER);
		this.add(container, COLUMN_SECOND, ROW_FIRST);
	}

	private void addEndTimeText(String text) {
		Text container = new Text(text);
		this.setHalignment(container, HPos.CENTER);
		this.add(container, COLUMN_SECOND, ROW_SECOND);
	}

	private void addStartTimeLabel(String text) {
		Text container = new Text(text);
		this.setHalignment(container, HPos.CENTER);
		this.add(container, COLUMN_SECOND, ROW_FIRST);
	}

	private void displayTaskDescription(Task task) {
		Text description = new Text(task.getDescription());

		description.setWrappingWidth(WRAPPING_WIDTH_DESCRIPTION);
		description.getStyleClass().add("descTaskStyle");

		this.add(description, COLUMN_THIRD, ROW_FIRST);
		this.setPrefHeight(0); // this fix unusual row height problems
	}

	private void checkAndDisplayTaskIcon(Task task) {
		int iconRow;
		HBox iconContainer = new HBox();

		if (task.isBlocking()) {
			iconRow = ROW_THIRD;
			iconContainer = createIconWithText(iconContainer,
					PATH_BLOCKING_ICON_IMAGE, LABEL_TEXT_BLOCKING);
		} else {
			iconRow = ROW_SECOND;
		}

		if (task.isDone()) {
			iconContainer = createIconWithText(iconContainer,
					PATH_DONE_ICON_IMAGE, LABEL_TEXT_DONE);
		}
		if (task.isPriority()) {
			iconContainer = createIconWithText(iconContainer,
					PATH_PRIORITY_ICON_IMAGE, LABEL_TEXT_PRIORITY);
		}
		if (task.isOverdue()) {
			iconContainer = createIconWithText(iconContainer,
					PATH_OVERDUE_ICON_IMAGE, LABEL_TEXT_OVERDUE);
		}
		if (task.isOverlapping()) {
			iconContainer = createIconWithText(iconContainer,
					PATH_OVERLAP_ICON_IMAGE, LABEL_TEXT_OVERLAP);
		}
		if (task.isDeadline()) {
			iconContainer = createIconWithText(iconContainer,
					PATH_DEADLINE_ICON_IMAGE, LABEL_TEXT_DEADLINE);
		}
		if (task.isError()) {
			iconContainer = createIconWithText(iconContainer,
					PATH_ERROR_ICON_IMAGE, LABEL_TEXT_ERROR);
		}
		
		iconContainer.getStyleClass().add("iconLabelStyle");
		this.add(iconContainer, COLUMN_THIRD, iconRow);
	}

	private HBox createIconWithText(HBox container, String imagePath,
			String text) {
		Image image = new Image(imagePath);
		Text iconText = new Text(text);
		ImageView icon = new ImageView();

		icon.setImage(image);
		container.getChildren().add(icon);
		container.getChildren().add(iconText);

		return container;
	}

	public int getTaskGridID() {
		return id;
	}
}
