/**
 * @author A0111921W
 */

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
			+ "\nOther Commands: delete, edit, done, redo, undo, #, find. Press CTRL+H for more details.";
	private static final String EMPTY_TASKVIEW_MESSAGE = "No tasks to display in this view!";
	private static final int INITIAL_INDEX = 0;

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
	 * This function handles ChangeListener for ListView to look for change in
	 * focus & display task
	 * 
	 * @author A0111921W
	 */
	private void listChangeListener() {

		hashTagList.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ov,
							String old_val, String new_val) {
						if (old_val != null && new_val != null
								&& !old_val.equals(new_val)) {
							logic.processCommand(new_val);
							displayTasks();
						}
					}
				});
	}

	private void labelChangeListener() {

		statusMessage.heightProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> ov,
							Number t, Number t1) {
						tc.setStageHeight(530 + container.getHeight());
					}
				});
	}

	private void initializeForms() {
		initializeTable();
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

				VBox vbox = (VBox) taskScrollPane.getContent();

				TaskGrid taskGrid = null;

				for (int i = index; i < vbox.getChildren().size(); i++) {
					if (vbox.getChildren().get(i) instanceof TaskGrid) {
						taskGrid = (TaskGrid) vbox.getChildren().get(i);
						if (taskGrid.getTaskGridID() == index) {
							break;
						}
					}
				}

				taskGrid.highlight();

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
		});
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

	private void initializeTable() {
		// Enable multiple selection for the table
		// taskTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		// taskTable.setPlaceholder(new Text(EMPTY_TASKVIEW_MESSAGE));
	}

	/**
	 * This function handles the status message,task and hashtag to display when
	 * user hit enter
	 * 
	 * @param event
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
		case Message.TYPE_SUCCESS:
			statusMessage.setText(message.getMessage());
			setFocusForHashTable(logic.getHashtagSelected());
			setFocusForTaskTableList(logic.getTasksSelected());
			displayHashTags();
			displayTasks();
			clearForm();
			break;
		case Message.TYPE_AUTOCOMPLETE:
			commandBar.setText(message.getMessage());
			commandBar.positionCaret(commandBar.getText().length());
			break;
		case Message.TYPE_ERROR:
			statusMessage.setText(message.getMessage());
			break;
		}
	}

	/**
	 * 
	 * @author A0112764J
	 */

	public void connectWithMainTaskCatalyst(TaskCatalyst tc) {
		this.tc = tc;
	}

	/**
	 * This function handles hotKey to execute a desired action that is done by
	 * user.
	 * 
	 * @param associatedText
	 * @author A0112764J
	 */
	public void handleHotKeys(final String associatedText) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Message message = logic.processCommand(associatedText);
				handleMessage(message);
			}
		});
	}

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
	 * @param event
	 * 
	 */
	public void handleTextFieldWhileUserTyping(KeyEvent event) {
		if (!event.getCode().equals(KeyCode.ENTER)
				&& !event.getCode().equals(KeyCode.CONTROL)) {
			Message message = logic.getMessageTyping(commandBar.getText());

			// This will remove autocomplete when hitting backspace during edit
			if (message.getType() == Message.TYPE_AUTOCOMPLETE
					&& !event.getCode().equals(KeyCode.BACK_SPACE)) {
				commandBar.setText(message.getMessage());
				commandBar.positionCaret(commandBar.getText().length());
				handleTextFieldWhileUserTyping(event);
			} else {
				statusMessage.setText(message.getMessage());
			}
		}
	}

	private void displayTasks() {
		VBox taskContainer = new VBox();
		String dateCategory;
		String prevDate = null;
		String currentDate = null;
		Date startDate;
		List<Task> task = logic.getList();

		taskContainer.setSpacing(10);
		// taskContainer.getStyleClass().add("vbox");

		if (task.isEmpty()) {
			StackPane container = new StackPane();
			container = createEmptyMessageLabel(container);
			taskScrollPane.setContent(container);
		} else {
			for (int i = 0; i < task.size(); i++) {
				Task currentTask = task.get(i);

				if (currentTask.getDateStart() != null) {

					// is it StartTime to endTime?
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
					dateCategory = new SimpleDateFormat(formatString)
							.format(startDate);

				} else {
					// Floating task
					dateCategory = "Someday";
				}
				currentDate = dateCategory;

				if (!currentDate.equals(prevDate)) {
					Label dateCategoryLabel = new Label(dateCategory);
					dateCategoryLabel.setMaxWidth(Double.MAX_VALUE);
					dateCategoryLabel.getStyleClass().add("dateCategoryStyle");
					taskContainer.getChildren().add(dateCategoryLabel);
				}

				prevDate = currentDate;
				taskContainer.getChildren().add(new TaskGrid(i, task.get(i)));
				taskContainer.getStyleClass().add("vbox");
				// taskContainer.setAlignment(javafx.geometry.Pos.CENTER);
				taskScrollPane.setContent(taskContainer);
			}
		}
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

	private void clearForm() {
		commandBar.clear();
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

	/**
	 * This function opens settings window (NOT IMPLEMENTED)
	 */
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
}
