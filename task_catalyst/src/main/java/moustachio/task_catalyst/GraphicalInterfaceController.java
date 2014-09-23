package moustachio.task_catalyst;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class GraphicalInterfaceController {
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

	private static LogicActual logic = new LogicActual();

	private static ObservableList<Task> taskToBeDisplayed = FXCollections
			.observableArrayList();
	private static ObservableList<String> hashTagToBeDisplayed = FXCollections
			.observableArrayList();

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		testInterface();
		initializeForms();
	}

	@FXML
	private void exitButtonAction() {
		Platform.exit();
	}

	private void initializeForms() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				commandBar.requestFocus();
			}
		});
		// displayTask();
		displayHashTags();
	}

	public void handleTextFieldOnAction(ActionEvent event) {
		// TextField entered was clicked, do something...
		Message message = logic.processCommand(commandBar.getText());
		switch (message.getType()) {
		case Message.TYPE_SUCCESS:
			statusMessage.setText(message.getMessage());
			displayHashTags();
			displayTask();
			clearForm();
			break;
		case Message.TYPE_ERROR:
			statusMessage.setText(message.getMessage());
			commandBar.clear();
			break;
		}
	}

	public void handleTextFieldWhileUserTyping() {
		Message message = logic.getMessageTyping(commandBar.getText());
		
		if (message.getType() == Message.TYPE_AUTOCOMPLETE) {
			commandBar.setText(message.getMessage());
		} else {
			statusMessage.setText(message.getMessage());
		}
	}

	private void displayHashTags() {
		hashTagList.setItems(getHashTagFromList());
	}

	public void clearForm() {
		commandBar.clear();
		statusMessage.setText(null);
	}

	private void displayTask() {
		idColumn.setCellValueFactory(new PropertyValueFactory<Task, Integer>(
				"id"));
		taskColumn.setCellValueFactory(new PropertyValueFactory<Task, String>(
				"description"));
		taskTable.setItems(getTaskFromList());
	}

	private ObservableList<Task> getTaskFromList() {
		List<Task> task = logic.getList();
		taskToBeDisplayed.clear();
		taskToBeDisplayed.addAll(task);
		return taskToBeDisplayed;
	}

	private ObservableList<String> getHashTagFromList() {
		List<String> DefaultHashTags = logic.getDefaultHashtags();
		List<String> hashTags = logic.getHashtags();

		hashTagToBeDisplayed.clear();
		hashTagToBeDisplayed.addAll(DefaultHashTags);
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
