package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@author A0111890L
/**
 * Edit Action relies on the TaskManager for retrieving and replacing tasks. It
 * is able to replace single tasks with multiple tasks if the user's task is
 * split. It provides autocomplete functionality when certain patterns are
 * matched, and uses Live Task Preview when the user is editing the task.
 */
public class Edit implements Action {
	private static final String[] DICTIONARY = { "edit" };

	private static final String EXECUTE_ERROR = "Invalid task number specified.";
	private static final String EXECUTE_SUCCESS = "Task successfully edited: %s";

	private static final String UNDO_ERROR = "There was an error restoring the task.";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";

	private static final String FORMAT_AUTOCOMPLETE = "edit %d %s";

	private static final String HINT_EXAMPLES = "\nExample: edit 1";
	private static final String HINT_VALID_TASK = "\nEdit: Hit enter after making your changes."
			+ HINT_EXAMPLES;
	private static final String HINT_INVALID_TASK = "Invalid task number specified."
			+ HINT_EXAMPLES;
	private static final String HINT_MESSAGE = "Edit: Hit space or enter after typing a valid task number to continue."
			+ HINT_EXAMPLES;

	private TaskBuilder taskBuilder;
	private static TaskManager taskManager = TaskManagerActual.getInstance();

	private List<Task> targetTasks;
	private List<Task> replacementTasks;

	private int taskNumber;

	private String parameters;
	private String targetDescriptionEdit;
	private String replacementDescriptionEdit;

	// Initialization Methods

	public Edit(String userCommand) {
		initializeComponents();
		initializeParameters(userCommand);
		populateTargetTasks();
		populateReplacementTasks();
	}

	private void initializeParameters(String userCommand) {
		this.parameters = TaskCatalystCommons.removeFirstWord(userCommand);
	}

	private void initializeComponents() {
		this.taskBuilder = new TaskBuilderAdvanced();
	}

	private void populateTargetTasks() {
		String taskNumberString = TaskCatalystCommons
				.getFirstWord(this.parameters);

		this.taskNumber = TaskCatalystCommons
				.parsePositiveInt(taskNumberString);

		Task targetTask = taskManager.getDisplayTask(this.taskNumber);

		boolean isTargetTaskValid = targetTask != null;

		if (isTargetTaskValid) {
			this.targetDescriptionEdit = targetTask.getDescriptionEdit();
			this.targetTasks = new ArrayList<Task>();
			this.targetTasks.add(targetTask);
		}
	}

	private void populateReplacementTasks() {
		String taskDescriptionString = TaskCatalystCommons
				.removeFirstWord(this.parameters);

		try {
			this.replacementTasks = this.taskBuilder
					.createTask(taskDescriptionString);
			this.replacementDescriptionEdit = taskDescriptionString;
		} catch (UnsupportedOperationException e) {
			this.replacementTasks = null;
			this.replacementDescriptionEdit = null;
		}
	}

	// Execution/Undo Methods

	@Override
	public Message execute() {
		MessageType messageType;
		String message;

		boolean isTargetTaskValid = (this.targetTasks != null);
		boolean isReplacementTaskValid = (this.replacementTasks != null);
		boolean isNeedAutocomplete = (isTargetTaskValid && !isReplacementTaskValid);
		boolean isWrongTaskNumber = !isTargetTaskValid;

		if (isNeedAutocomplete) {
			messageType = MessageType.AUTOCOMPLETE;
			message = String.format(FORMAT_AUTOCOMPLETE, this.taskNumber,
					this.targetDescriptionEdit);

			return new Message(messageType, message);
		}

		if (isWrongTaskNumber) {
			messageType = MessageType.ERROR;
			message = String.format(EXECUTE_ERROR) + HINT_EXAMPLES;

			return new Message(messageType, message);
		}

		String taskDescription = TaskCatalystCommons
				.getDisplayString(this.replacementDescriptionEdit);

		return replace(this.targetTasks, this.replacementTasks,
				taskDescription, EXECUTE_SUCCESS, EXECUTE_ERROR);
	}

	@Override
	public Message undo() {
		String taskDescription = TaskCatalystCommons
				.getDisplayString(this.targetDescriptionEdit);

		return replace(this.replacementTasks, this.targetTasks,
				taskDescription, UNDO_SUCCESS, UNDO_ERROR);
	}

	private Message replace(List<Task> targetTasks,
			List<Task> replacementTasks, String taskDescription,
			String successFormat, String errorFormat) {
		MessageType messageType;
		String message;

		int tasksDeleted = taskManager.removeTasks(targetTasks);
		int tasksAdded = taskManager.addTasks(replacementTasks);

		boolean isDeleteSuccess = tasksDeleted > 0;
		boolean isAddSuccess = tasksAdded > 0;
		boolean isSuccess = isDeleteSuccess && isAddSuccess;

		if (isSuccess) {
			messageType = MessageType.SUCCESS;
			message = String.format(successFormat, taskDescription);
		} else {
			messageType = MessageType.ERROR;
			message = String.format(errorFormat);
		}

		return new Message(messageType, message);
	}

	public static Message getHint(String userCommand) {
		MessageType messageType;
		String message;

		int taskNumber = getTaskNumber(userCommand);
		String description = getDescription(userCommand);
		Task targetTask = getTargetTask(taskNumber);

		boolean isHasDescription = !description.isEmpty();
		boolean isEndsWithSpace = userCommand.endsWith(" ");
		boolean isValidTask = targetTask != null;
		boolean isPositiveTaskNumber = taskNumber > 0;
		boolean isInvalidTask = !isValidTask && isPositiveTaskNumber
				&& (isEndsWithSpace || isHasDescription);
		boolean isAutocomplete = isValidTask && isEndsWithSpace
				&& !isHasDescription;
		boolean isBeingEdited = isValidTask && isHasDescription;

		if (isInvalidTask) {
			messageType = MessageType.HINT;
			message = getInvalidTaskMessage();
		} else if (isAutocomplete) {
			messageType = MessageType.AUTOCOMPLETE;
			message = getAutocompleteMessage(taskNumber, targetTask);
		} else if (isBeingEdited) {
			try {
				messageType = MessageType.HINT;
				message = getLiveTaskPreviewMessage(userCommand, taskNumber);
			} catch (UnsupportedOperationException e) {
				messageType = MessageType.ERROR;
				message = getErrorMessage(e);
			}
			message += HINT_VALID_TASK;
		} else {
			messageType = MessageType.HINT;
			message = HINT_MESSAGE;
		}

		return new Message(messageType, message);
	}

	private static Task getTargetTask(int taskNumber) {
		return taskManager.getDisplayTask(taskNumber);
	}

	private static int getTaskNumber(String userCommand) {
		String taskNumberString;
		taskNumberString = TaskCatalystCommons.removeFirstWord(userCommand);
		taskNumberString = TaskCatalystCommons.getFirstWord(taskNumberString);

		int taskNumber = TaskCatalystCommons.parsePositiveInt(taskNumberString);

		return taskNumber;
	}

	private static String getInvalidTaskMessage() {
		String message = HINT_INVALID_TASK;

		return message;
	}

	private static String getAutocompleteMessage(int taskNumber, Task targetTask) {
		String taskDescription = targetTask.getDescriptionEdit();

		String message = String.format(FORMAT_AUTOCOMPLETE, taskNumber,
				taskDescription);

		return message;
	}

	private static String getLiveTaskPreviewMessage(String userCommand,
			int taskNumber) {
		String editAndTaskNumber = "edit " + taskNumber + " ";
		String userInput = userCommand.replace(editAndTaskNumber, "");
		String liveTaskPreviewMessage = TaskCatalystCommons
				.getDisplayString(userInput);

		return liveTaskPreviewMessage;
	}

	private static String getErrorMessage(UnsupportedOperationException e) {
		String errorMessage = e.getMessage();

		return errorMessage;
	}

	private static String getDescription(String userCommand) {
		String description;
		description = TaskCatalystCommons.removeFirstWord(userCommand);
		description = TaskCatalystCommons.removeFirstWord(description);
		description = description.trim();

		return description;
	}

	// Other Methods

	public static boolean isThisAction(String command) {
		List<String> dictionaryList = Arrays.asList(DICTIONARY);

		boolean isContainsCommand = dictionaryList.contains(command);

		return isContainsCommand;
	}

	public static String[] getDictionary() {
		return DICTIONARY;
	}

	@Override
	public boolean isUndoable() {
		return true;
	}
}