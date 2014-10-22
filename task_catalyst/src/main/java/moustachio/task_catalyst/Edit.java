package moustachio.task_catalyst;

import java.util.Arrays;

public class Edit extends Action {

	private static final String[] DICTIONARY = { "edit" };

	private static final String EXECUTE_ERROR = "There was an error editing the task.";
	private static final String EXECUTE_SUCCESS = "Task successfully edited: %s";
	private static final String UNDO_ERROR = "There was an error restoring the task.";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";

	private TaskBuilder taskBuilder;
	private TaskManager taskManager;

	private Task targetTask;
	private Task replacementTask;

	public Edit(String userCommand) {
		taskBuilder = new TaskBuilderAdvanced();
		taskManager = TaskManagerActual.getInstance();
		targetTask = taskBuilder.createTask(userCommand);

		String taskNumberAndContent = TaskCatalystCommons
				.removeFirstWord(userCommand);
		String taskNumberString = TaskCatalystCommons
				.getFirstWord(taskNumberAndContent);
		int taskNumber = TaskCatalystCommons.parseInt(taskNumberString);

		targetTask = taskManager.getDisplayTask(taskNumber);

		String userInput = TaskCatalystCommons
				.removeFirstWord(taskNumberAndContent);

		replacementTask = taskBuilder.createTask(userInput);
	}

	@Override
	public Message execute() {
		if (targetTask == null || replacementTask == null) {
			int type = Message.TYPE_ERROR;
			String message = String.format(EXECUTE_ERROR);

			return new Message(type, message);
		}
		return replace(targetTask, replacementTask, EXECUTE_SUCCESS,
				EXECUTE_ERROR);
	}

	@Override
	public Message undo() {
		return replace(replacementTask, targetTask, UNDO_SUCCESS, UNDO_ERROR);
	}

	private Message replace(Task targetTask, Task replacementTask,
			String successFormat, String errorFormat) {

		boolean isDeleteSuccess = taskManager.removeTask(targetTask);
		boolean isAddSuccess = taskManager.addTask(replacementTask);
		int type;
		String message;
		if (isDeleteSuccess && isAddSuccess) {
			type = Message.TYPE_SUCCESS;
			message = String.format(successFormat,
					replacementTask.getDescription());
		} else {
			type = Message.TYPE_ERROR;
			message = String.format(errorFormat);
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