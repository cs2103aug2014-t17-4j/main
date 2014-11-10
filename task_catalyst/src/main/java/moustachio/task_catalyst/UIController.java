package moustachio.task_catalyst;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//@author A0111921W

/**
 * UIController is the main controller for the TaskCatalyst UI.
 */

public class UIController {
	@FXML
	private BorderPane rootBorderPane;
	@FXML
	private ScrollPane taskScrollPane;
	@FXML
	private ImageView programTitle;
	@FXML
	private ListView<String> hashTagList;
	@FXML
	private Label statusMessage;
	@FXML
	private TextField commandBar;
	@FXML
	private Button settingsButton;
	@FXML
	private Button exitButton;
	@FXML
	private VBox container;

	private static LogicActual logic;

	private static ObservableList<String> hashTagToBeDisplayed = FXCollections
			.observableArrayList();

	private TaskCatalyst tc;

	private static final String STATUS_BAR_MESSAGE = "Type something to begin adding a task."
			+ "\nOther Commands: delete, edit, done, restore, redo, undo, #, find. Press CTRL+H for more details.";
	private static final String EMPTY_TASKVIEW_MESSAGE = "No tasks to display in this view!";
	
	private static final int INITIAL_INDEX = 0;
	private static final int STAGE_HEIGHT = 540;
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		logic = new LogicActual();
		testInterface();
		initializeForms();
		listChangeListener();
		labelChangeListener();

	}

	@FXML
	public void exitButtonAction() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				tc.getStage().hide();
				commandBar.requestFocus();
			}
		});
	}

	/**
	 * This method add a ChangeListener for ListView to look for change in focus
	 * & display task
	 */
	//@author A0111921W - Adapted from ORACLE
	//http://docs.oracle.com/javafx/2/ui_controls/list-view.htm
	private void listChangeListener() {

		hashTagList.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ov,
							String old_val, String new_val) {
						if (old_val != null && new_val != null
								&& !old_val.equals(new_val)) {
							Message message = logic.processCommand(new_val);
							if (message.getType() == MessageType.SUCCESS) {
								statusMessage.setText(message.getMessage());
							}
							displayTasks();
						}
					}
				});
	}

	/**
	 * This method add a ChangeListener for statusMessage to look for change in
	 * height and adjust stage height accordingly
	 */
	private void labelChangeListener() {

		statusMessage.heightProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> ov,
							Number t, Number t1) {
						tc.setStageHeight(STAGE_HEIGHT + container.getHeight());
					}
				});
	}

	private void initializeForms() {
		statusMessage.setWrapText(true);
		taskScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				statusMessage.setText(STATUS_BAR_MESSAGE);
				setFocusForHashTable(INITIAL_INDEX);
				commandBar.requestFocus();
			}
		});
		displayTasks();
		displayHashTags();
	}

	private void setFocusForTaskTable(int index) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (index == -1) {
					return;
				}

				TaskGrid taskGrid = highlightID(index);
				scrollToTask(index, taskGrid);
			}
		});
	}

	private void scrollToTask(int index, TaskGrid taskGrid) {
		// get vbox's height
		double height = taskScrollPane.getContent().getBoundsInLocal()
				.getHeight();

		double y = taskGrid.getBoundsInParent().getMaxY();

		if (y < 10) {
			setFocusForTaskTable(index);
			return;
		}

		y = y + ((y / height) - 0.5) * taskScrollPane.getHeight()
				- taskGrid.getHeight() / 2;

		taskScrollPane.setVvalue(y / height);
	}

	private TaskGrid highlightID(int index) {
		VBox taskContainer = (VBox) taskScrollPane.getContent();

		TaskGrid taskGrid = null;

		// get task ID label location
		for (int i = index; i < taskContainer.getChildren().size(); i++) {
			if (taskContainer.getChildren().get(i) instanceof TaskGrid) {
				taskGrid = (TaskGrid) taskContainer.getChildren().get(i);
				
				if (taskGrid.getTaskGridID() == index) {
					break;
				}
			}
		}

		// highlight id label
		taskGrid.highlight();
		
		return taskGrid;
	}

	private void setFocusForTaskTableList(List<Integer> list) {
		for (int index : list) {
			setFocusForTaskTable(index);
		}
	}

	private void setFocusForHashTable(int index) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				hashTagList.scrollTo(index);
				hashTagList.getSelectionModel().select(index);
			}
		});
	}

	/**
	 * This function handles the status message,task and hashtag to display when
	 * user hit enter
	 * 
	 * @param event The event generated by the action.
	 */
	public void handleTextFieldOnAction(ActionEvent event) {
		Message message = logic.processCommand(commandBar.getText());
		handleMessage(message);
	}

	/**
	 * This function handles the actions to execute with different message type
	 * 
	 * @param message
	 */
	private void handleMessage(Message message) {
		switch (message.getType()) {
			case SUCCESS:
				statusMessage.setText(message.getMessage());
				setFocusForHashTable(logic.getHashtagSelected());
				setFocusForTaskTableList(logic.getTasksSelected());
				displayHashTags();
				displayTasks();
				commandBar.clear();
				break;
			case AUTOCOMPLETE:
				commandBar.setText(message.getMessage());
				commandBar.positionCaret(commandBar.getText().length());
				break;
			case ERROR:
				statusMessage.setText(message.getMessage());
				break;
			default:
				break;
		}
	}

	/**
	 * For hotkey to scroll up tasks list
	 */
	public void scrollTaskUp() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (!hashTagList.isFocused()) {
					double scrollAmt = taskScrollPane.getVvalue()
							- taskScrollPane.getHeight()
							/ 4
							/ taskScrollPane.getContent().getBoundsInLocal()
									.getHeight();
					taskScrollPane.setVvalue(scrollAmt);
				}
			}
		});
	}

	/**
	 * For hotkey to scroll down tasks list
	 */
	public void scrollTaskDown() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (!hashTagList.isFocused()) {
					double scrollAmt = taskScrollPane.getVvalue()
							+ taskScrollPane.getHeight()
							/ 4
							/ taskScrollPane.getContent().getBoundsInLocal()
									.getHeight();
					;
					taskScrollPane.setVvalue(scrollAmt);
				}
			}
		});
	}

	/**
	 * For hotkey to scroll up hashtag list
	 */
	public void scrollHashtagUp() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				int now = hashTagList.getSelectionModel().getSelectedIndex();
				hashTagList.getSelectionModel().select(Math.max(0, now - 1));
			}
		});
	}
	
	public void scrollHashtagDown() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				int now = hashTagList.getSelectionModel().getSelectedIndex();
				hashTagList.getSelectionModel().select(now + 1);
			}
		});
	}
	
	/**
	 * For CTRL + D, this method will paste the text into command bar
	 * @param pasted The text copied from clipboard.
	 */
	public void setCommandBar(String pasted) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				commandBar.setText(pasted);
				commandBar.requestFocus();
				commandBar.positionCaret(commandBar.getText().length());
			}
		});
	}

	/**
	 * This function handles the status message to display and provide
	 * autocomplete for edit command while using is typing
	 * 
	 * @param event The event generated by the keypress.
	 * 
	 */
	public void handleTextFieldWhileUserTyping(KeyEvent event) {
		// status message will be displayed when user didn't press enter or
		// control
		if (!event.getCode().equals(KeyCode.ENTER)
				&& !event.getCode().equals(KeyCode.CONTROL)) {
			Message message = logic.getMessageTyping(commandBar.getText());

			// This will remove autocomplete when hitting backspace during edit
			if (message.getType() == MessageType.AUTOCOMPLETE
					&& !event.getCode().equals(KeyCode.BACK_SPACE)) {
				commandBar.setText(message.getMessage());
				commandBar.positionCaret(commandBar.getText().length());
				handleTextFieldWhileUserTyping(event);
			} else {
				statusMessage.setText(message.getMessage());
			}
		}
	}

	/**
	 * This method handles displaying of date category and tasks and set into
	 * the scroll pane
	 */
	private void displayTasks() {
		VBox taskContainer = new VBox();
		String dateCategory;
		String prevDate = null;
		String currentDate = null;
		Date startDate;
		List<Task> task = logic.getList();

		if (task.isEmpty()) {
			StackPane container = new StackPane();
			container = createEmptyMessageLabel(container);
			taskScrollPane.setContent(container);
		} else {
			for (int i = 0; i < task.size(); i++) {
				Task currentTask = task.get(i);

				if (currentTask.getDateStart() != null) {
					// isRange: Task with start and end date
					// isBlocking: Task with multiple date
					if (currentTask.isRange()) {
						startDate = currentTask.getDateStart();
					} else if (currentTask.isBlocking()) {
						startDate = currentTask.getNextDate();
						if (startDate == null) {
							startDate = currentTask.getDateEnd();
						}
					} else {
						startDate = currentTask.getDateStart();
					}

					dateCategory = setDateCategory(startDate);

				} else if(currentTask.isError()) {
					dateCategory = "Error";
				} else {
					// Floating task
					dateCategory = "Someday";
				}
				currentDate = dateCategory;

				// Ignore repeated dates
				if (!currentDate.equals(prevDate)) {
					Label dateCategoryLabel = new Label(dateCategory);
					dateCategoryLabel.setMaxWidth(Double.MAX_VALUE);
					dateCategoryLabel.getStyleClass().add("dateCategoryStyle");
					taskContainer.getChildren().add(dateCategoryLabel);
				}

				prevDate = currentDate;

				taskContainer.setSpacing(10);
				taskContainer.getStyleClass().add("vbox");
				taskContainer.getChildren().add(new TaskGrid(i, task.get(i)));
				taskScrollPane.setContent(taskContainer);
			}
		}
	}

	private String setDateCategory(Date startDate) {
		String dateCategory;
		String formatString = "dd MMMM yyyy";

		if (TaskCatalystCommons.isYesterday(startDate)) {
			formatString += "' (Yesterday)'";
		} else if (TaskCatalystCommons.isToday(startDate)) {
			formatString += "' (Today)'";
		} else if (TaskCatalystCommons.isTomorrow(startDate)) {
			formatString += "' (Tomorrow)'";
		} else if (TaskCatalystCommons.isThisWeek(startDate)) {
			formatString += " (E)";
		}
		dateCategory = new SimpleDateFormat(formatString).format(startDate);
		return dateCategory;
	}

	private StackPane createEmptyMessageLabel(StackPane container) {
		Label messageLabel = new Label(EMPTY_TASKVIEW_MESSAGE);
		messageLabel.setPrefHeight(taskScrollPane.getPrefHeight());
		container.getChildren().add(messageLabel);
		container.setAlignment(Pos.CENTER);
		return container;
	}

	private void displayHashTags() {
		hashTagList.setItems(getHashTagFromList());
	}

	private ObservableList<String> getHashTagFromList() {
		List<String> hashTags = logic.getHashtags();

		hashTagToBeDisplayed.clear();
		hashTagToBeDisplayed.addAll(hashTags);

		return hashTagToBeDisplayed;
	}

	private void testInterface() {
		assert rootBorderPane != null : "fx:id=\"rootBorderPane\" was not injected: check your FXML file 'interface.fxml'.";
		assert taskScrollPane != null : "fx:id=\"taskScrollPane\" was not injected: check your FXML file 'interface.fxml'.";
		assert programTitle != null : "fx:id=\"rootBorderPane\" was not injected: check your FXML file 'interface.fxml'.";
		assert hashTagList != null : "fx:id=\"hashTagList\" was not injected: check your FXML file 'interface.fxml'.";
		assert statusMessage != null : "fx:id=\"statusMessage\" was not injected: check your FXML file 'interface.fxml'.";
		assert commandBar != null : "fx:id=\"commandBar\" was not injected: check your FXML file 'interface.fxml'.";
		assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'interface.fxml'.";
	}
	
	//@author A0111921W - unused
	//There was too little settings to implement
	@FXML
	private void openSettingsWindow() {
		Pane myPane;
		try {
			myPane = FXMLLoader.load(getClass().getResource("settings.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(myPane));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//@author A0112764J
	public void connectWithMainTaskCatalyst(TaskCatalyst tc) {
		this.tc = tc;
	}

	/**
	 * This function handles hotKey to execute a desired action that is done by
	 * user.
	 * 
	 * @param associatedText The command associated to the hotkey.
	 */
	//@author A0112764J
	public void handleHotKeys(final String associatedText) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Message message = logic.processCommand(associatedText);
				handleMessage(message);
			}
		});
	}
}

//@author A0111921W
// <?xml version="1.0" encoding="UTF-8"?>
//
// <?import javafx.scene.paint.*?>
// <?import javafx.scene.effect.*?>
// <?import javafx.scene.image.*?>
// <?import javafx.scene.text.*?>
// <?import javafx.geometry.*?>
// <?import javafx.scene.control.*?>
// <?import java.lang.*?>
// <?import javafx.scene.layout.*?>
// <?import javafx.scene.layout.AnchorPane?>
//
// <VBox styleClass="theme" stylesheets="@../css/DarkTheme.css"
// xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1"
// fx:controller="moustachio.task_catalyst.UIController">
// <children>
// <BorderPane fx:id="rootBorderPane" prefHeight="520.0" prefWidth="620.0"
// stylesheets="@../css/DarkTheme.css">
// <top>
// <HBox alignment="TOP_RIGHT" prefHeight="20.0" prefWidth="200.0"
// spacing="10.0">
// <children>
// <ImageView fx:id="programTitle" fitHeight="24.0" fitWidth="173.0" />
// <HBox prefHeight="24.0" prefWidth="500.0" styleClass="theme" />
// <Button id="exitButton" fx:id="exitButton" alignment="CENTER"
// contentDisplay="CENTER" minHeight="-Infinity" minWidth="-Infinity"
// mnemonicParsing="false" onAction="#exitButtonAction" prefHeight="24.0"
// prefWidth="24.0" textAlignment="CENTER" HBox.hgrow="ALWAYS" />
// </children>
// <BorderPane.margin>
// <Insets bottom="5.0" left="5.0" right="5.0" top="5.0" />
// </BorderPane.margin>
// </HBox>
// </top>
// <bottom>
// <AnchorPane maxHeight="1.7976931348623157E308" minHeight="-Infinity"
// prefWidth="620.0">
// <children>
// <VBox id="statusMessageLabel" fx:id="container" prefWidth="618.0"
// AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0"
// AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0"
// BorderPane.alignment="CENTER">
// <children>
// <TextField id="cmdTextField" fx:id="commandBar"
// onAction="#handleTextFieldOnAction"
// onKeyReleased="#handleTextFieldWhileUserTyping" prefHeight="0.0"
// prefWidth="617.0" />
// </children>
// </VBox>
// </children>
// </AnchorPane>
// </bottom>
// <center>
// <SplitPane dividerPositions="0.2" minHeight="-Infinity" prefWidth="345.0"
// styleClass="theme" BorderPane.alignment="CENTER">
// <items>
// <ListView fx:id="hashTagList" prefHeight="200.0" prefWidth="200.0"
// styleClass="lightList" />
// <ScrollPane fx:id="taskScrollPane" fitToWidth="true" prefHeight="410.0"
// prefWidth="470.0" />
// </items>
// </SplitPane>
// </center>
// </BorderPane>
// <VBox fx:id="container" prefWidth="620.0"
// stylesheets="@../css/DarkTheme.css">
// <children>
// <Label fx:id="statusMessage" prefWidth="618.0">
// <VBox.margin>
// <Insets left="5.0" right="5.0" />
// </VBox.margin>
// </Label>
// </children>
// </VBox>
// </children>
// </VBox>
