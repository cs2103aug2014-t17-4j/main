package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Edit extends Action {

	private static final String HINT_SYNTAX = "\nSyntax: edit <task number>";

	private static final String FORMAT_AUTOCOMPLETE = "edit %d %s";

	private static final String HINT_VALID_TASK = "\nEdit: Hit enter after making your changes."
			+ HINT_SYNTAX;

	private static final String HINT_INVALID_TASK = "Invalid task number specified."
			+ HINT_SYNTAX;

	private static final String HINT_MESSAGE = "Edit: Hit space or enter after typing a valid task number to continue."
			+ HINT_SYNTAX;

	private static final String[] DICTIONARY = { "edit" };

	private static final String EXECUTE_ERROR = "Invalid task number specified.";
	private static final String EXECUTE_SUCCESS = "Task successfully edited: %s";
	private static final String UNDO_ERROR = "There was an error restoring the task.";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";

	private TaskBuilder taskBuilder;
	private static TaskManager taskManager = TaskManagerActual.getInstance();

	private List<Task> targetTasks;
	private List<Task> replacementTasks;

	private int taskNumber;

	private String descriptionEdit;

	private String originalDescription;
	private String newDescription;

	public Edit(String userCommand) {
		taskBuilder = new TaskBuilderAdvanced();

		String taskNumberAndContent = TaskCatalystCommons
				.removeFirstWord(userCommand);
		String taskNumberString = TaskCatalystCommons
				.getFirstWord(taskNumberAndContent);
		taskNumber = TaskCatalystCommons.parsePositiveInt(taskNumberString);

		Task targetTask = taskManager.getDisplayTask(taskNumber);
		if (targetTask != null) {
			descriptionEdit = targetTask.getDescriptionEdit();
			originalDescription = targetTask.getDescription();
			targetTasks = new ArrayList<Task>();
			targetTasks.add(targetTask);
		}

		String userInput = TaskCatalystCommons
				.removeFirstWord(taskNumberAndContent);

		try {
			replacementTasks = taskBuilder.createTask(userInput);
			newDescription = TaskCatalystCommons.getFriendlyString(userInput);
		} catch (UnsupportedOperationException e) {
			replacementTasks = null;
			newDescription = null;
		}
	}

	@Override
	public Message execute() {

		if (targetTasks != null && replacementTasks == null) {
			int type = Message.TYPE_AUTOCOMPLETE;
			String message = String.format(FORMAT_AUTOCOMPLETE, taskNumber,
					descriptionEdit);

			return new Message(type, message);
		}

		if (targetTasks == null) {
			int type = Message.TYPE_ERROR;
			String message = String.format(EXECUTE_ERROR) + HINT_SYNTAX;

			return new Message(type, message);
		}
		String taskDescription = TaskCatalystCommons
				.getFriendlyString(newDescription);
		for (Task task : this.targetTasks) {
			System.out.println("Giving Removing: " + task.getDescription());
		}
		for (Task task : this.replacementTasks) {
			System.out.println("Giving Adding: " + task.getDescription());
		}
		return replace(this.targetTasks, this.replacementTasks,
				taskDescription, EXECUTE_SUCCESS, EXECUTE_ERROR);
	}

	@Override
	public Message undo() {
		String taskDescription = TaskCatalystCommons
				.getFriendlyString(originalDescription);

		return replace(this.replacementTasks, this.targetTasks,
				taskDescription, UNDO_SUCCESS, UNDO_ERROR);
	}

	private Message replace(List<Task> targetTasks,
			List<Task> replacementTasks, String taskDescription,
			String successFormat, String errorFormat) {

		int tasksDeleted = taskManager.removeTasks(targetTasks);
		boolean isDeleteSuccess = tasksDeleted > 0;
		int tasksAdded = taskManager.addTasks(replacementTasks);
		boolean isAddSuccess = tasksAdded > 0;

		int type;
		String message;

		if (isDeleteSuccess && isAddSuccess) {
			type = Message.TYPE_SUCCESS;
			message = String.format(successFormat, taskDescription);
		} else {
			type = Message.TYPE_ERROR;
			message = String.format(errorFormat);
		}

		return new Message(type, message);
	}

	public static Message getHint(String userCommand) {
		int type;
		String message;

		type = Message.TYPE_HINT;
		message = HINT_MESSAGE;

		String taskNumberString;
		taskNumberString = TaskCatalystCommons.removeFirstWord(userCommand);
		taskNumberString = TaskCatalystCommons.getFirstWord(taskNumberString);
		int taskNumber = TaskCatalystCommons.parsePositiveInt(taskNumberString);

		Task editTask = taskManager.getDisplayTask(taskNumber);

		String furtherParameters;
		furtherParameters = TaskCatalystCommons.removeFirstWord(userCommand);
		furtherParameters = TaskCatalystCommons
				.removeFirstWord(furtherParameters);
		furtherParameters = furtherParameters.trim();

		boolean hasFurtherParameters = !furtherParameters.isEmpty();
		boolean endsWithSpace = userCommand.endsWith(" ");
		boolean isValidTask = editTask != null;
		boolean isPositiveTaskNumber = taskNumber > 0;

		boolean isInvalidTask = !isValidTask && isPositiveTaskNumber
				&& (endsWithSpace || hasFurtherParameters);
		boolean isAutocomplete = isValidTask && endsWithSpace
				&& !hasFurtherParameters;
		boolean isBeingEdited = isValidTask && hasFurtherParameters;

		if (isInvalidTask) {
			message = HINT_INVALID_TASK;
		} else if (isAutocomplete) {
			String taskDescription = editTask.getDescriptionEdit();

			type = Message.TYPE_AUTOCOMPLETE;
			message = String.format(FORMAT_AUTOCOMPLETE, taskNumber,
					taskDescription);
		} else if (isBeingEdited) {
			try {
				message = TaskCatalystCommons.getFriendlyString(userCommand
						.replace("edit " + taskNumber + " ", ""));
				type = Message.TYPE_HINT;
			} catch (UnsupportedOperationException e) {
				message = e.getMessage();
				type = Message.TYPE_ERROR;
			}

			type = Message.TYPE_HINT;
			message += HINT_VALID_TASK;
		}

		return new Message(type, message);
	}

	public static boolean isThisAction(String command) {
		return Arrays.asList(DICTIONARY).contains(command);
	}

	public static String[] getDictionary() {
		return DICTIONARY;
	}

	@Override
	public boolean isUndoable() {
		return true;
	}
}