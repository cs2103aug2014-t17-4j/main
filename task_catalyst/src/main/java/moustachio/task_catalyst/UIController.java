package moustachio.task_catalyst;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UIController {
	@FXML
	private BorderPane rootBorderPane;
	@FXML
	private ImageView programTitle;
	@FXML
	private TableView<Task> taskTable;
	@FXML
	private TableColumn<Task, Integer> idColumn;
	@FXML
	private TableColumn<Task, String> taskColumn;
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

	private static LogicActual logic;

	private static ObservableList<Task> taskToBeDisplayed = FXCollections
			.observableArrayList();
	private static ObservableList<String> hashTagToBeDisplayed = FXCollections
			.observableArrayList();

	private TaskCatalyst tc;
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		logic = new LogicActual();
		testInterface();
		initializeForms();
	}

	@FXML
	private void exitButtonAction() {
		Platform.exit();
	}

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

	private void initializeForms() {
		initializeTable();
		statusMessage.setWrapText(true);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				hashTagList.scrollTo(0);
				hashTagList.getSelectionModel().select(0);
				commandBar.requestFocus();
			}
		});
		displayTask();
		displayHashTags();
	}

	private void setFocusForTaskTable(int index) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				taskTable.scrollTo(index);
				taskTable.getSelectionModel().select(index);
			}
		});
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
		taskTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// Set message shown when there's no content
		// taskTable.setPlaceholder();
		// taskTable.setPlaceholder(new Text("Your localized text here"));
	}

	public void handleTextFieldOnAction(ActionEvent event) {
		Message message = logic.processCommand(commandBar.getText());
		switch (message.getType()) {
		case Message.TYPE_SUCCESS:
			statusMessage.setText(message.getMessage());

			if (commandBar.getText().toLowerCase().startsWith("edit")) {
				String[] getIndexForFocus = commandBar.getText().split("\\s");
				if (getIndexForFocus.length >= 1) {
					setFocusForTaskTable(Integer.valueOf(getIndexForFocus[1]));
				}
			}
			setFocusForHashTable(logic.getHashtagSelected());
			displayHashTags();
			displayTask();
			clearForm();
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
	 * 
	 * @author A0112764
	 */
	public void handleHotKeys(final String associatedText) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Message message = logic.processCommand(associatedText);
				switch (message.getType()) {
				case Message.TYPE_SUCCESS:
					statusMessage.setText(message.getMessage());

					displayHashTags();
					displayTask();
					break;
				case Message.TYPE_ERROR:
					statusMessage.setText(message.getMessage());
					break;
				case Message.TYPE_AUTOCOMPLETE:
					statusMessage.setText(message.getMessage());
					break;
				}
			}

		});
	}

	public void handleTextFieldWhileUserTyping(KeyEvent event) {
		if (!event.getCode().equals(KeyCode.ENTER)) {
			Message message = logic.getMessageTyping(commandBar.getText());

			if (message.getType() == Message.TYPE_AUTOCOMPLETE) {
				commandBar.setText(message.getMessage());
				commandBar.positionCaret(commandBar.getText().length());
				handleTextFieldWhileUserTyping(event);
			} else {
				statusMessage.setText(message.getMessage());
			}
		}
	}

	private void displayHashTags() {
		hashTagList.setItems(getHashTagFromList());
	}

	private void clearForm() {
		commandBar.clear();
	}

	/**
	 * 
	 * @author A0111921W
	 */
	private void displayTask() {
		if (logic.getList() != null) {
			idColumn.setCellValueFactory(new Callback<CellDataFeatures<Task, Integer>, ObservableValue<Integer>>() {
				@Override
				public ObservableValue<Integer> call(
						CellDataFeatures<Task, Integer> p) {
					return new ReadOnlyObjectWrapper<Integer>(
							(Integer) taskTable.getItems()
									.indexOf(p.getValue()) + 1);
				}
			});

			taskColumn
					.setCellValueFactory(new PropertyValueFactory<Task, String>(
							"description"));

			taskColumn
					.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
						@Override
						public TableCell<Task, String> call(
								TableColumn<Task, String> arg0) {
							return new TableCell<Task, String>() {
								private Text text;

								@Override
								public void updateItem(String item,
										boolean empty) {
									super.updateItem(item, empty);
									if (!isEmpty()) {
										text = new Text(item.toString());
										text.setWrappingWidth(taskColumn
												.getWidth());
										this.setWrapText(true);

										setGraphic(text);
									}
									if (item != null){
										String cssSelector = null;
										Task task = logic.getList().get(this.getIndex());
										
										switch(task.getHighlightType()){
										case LAST_ADDED:
											cssSelector = "isTaskLastAdded";
											break;
										case PRIORITY:
											cssSelector = "isPriority";
											break;
										case OVERLAP:
											cssSelector = "isOverlapStatic";
											break;
										case PRIORITY_OVERLAP:
											cssSelector = "isPriorityOverlapStatic";
											break;
										default:
											cssSelector = null;
											break;
										}
										this.getTableRow().getStyleClass().add(cssSelector);
									}
								}
							};
						}
					});
			idColumn.setSortable(false);
			taskColumn.setSortable(false);
			taskTable.setItems(getTaskFromList());
		}
	}

	private ObservableList<Task> getTaskFromList() {
		List<Task> task = logic.getList();

		taskToBeDisplayed.clear();
		taskToBeDisplayed.addAll(task);
		return taskToBeDisplayed;
	}

	private ObservableList<String> getHashTagFromList() {
		List<String> hashTags = logic.getHashtags();

		hashTagToBeDisplayed.clear();
		hashTagToBeDisplayed.addAll(hashTags);

		return hashTagToBeDisplayed;
	}

	private void testInterface() {
		assert rootBorderPane != null : "fx:id=\"rootBorderPane\" was not injected: check your FXML file 'interface.fxml'.";
		assert programTitle != null : "fx:id=\"rootBorderPane\" was not injected: check your FXML file 'interface.fxml'.";
		assert taskTable != null : "fx:id=\"taskTable\" was not injected: check your FXML file 'interface.fxml'.";
		assert idColumn != null : "fx:id=\"idColumn\" was not injected: check your FXML file 'interface.fxml'.";
		assert taskColumn != null : "fx:id=\"taskColumn\" was not injected: check your FXML file 'interface.fxml'.";
		assert hashTagList != null : "fx:id=\"hashTagList\" was not injected: check your FXML file 'interface.fxml'.";
		assert statusMessage != null : "fx:id=\"statusMessage\" was not injected: check your FXML file 'interface.fxml'.";
		assert commandBar != null : "fx:id=\"commandBar\" was not injected: check your FXML file 'interface.fxml'.";
		assert settingsButton != null : "fx:id=\"settingsButton\" was not injected: check your FXML file 'interface.fxml'.";
		assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'interface.fxml'.";
	}

}
