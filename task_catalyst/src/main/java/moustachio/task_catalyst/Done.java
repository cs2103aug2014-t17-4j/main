package moustachio.task_catalyst;

import java.util.Arrays;

public class Done extends Action {

	private static final String[] DICTIONARY = { "done", "complete" };

	private static final String EXECUTE_ERROR = "There was an error completing the task.";
	private static final String EXECUTE_SUCCESS = "Task successfully completed: %s";
	private static final String UNDO_ERROR = "There was an error restoring the task.";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";

	private TaskManager taskManager;
	private Task task;

	public Done(String userCommand) {
		taskManager = TaskManagerActual.getInstance();
		String taskNumberString = TaskCatalystCommons
				.removeFirstWord(userCommand);
		int taskNumber = TaskCatalystCommons.parseInt(taskNumberString);
		task = taskManager.getDisplayTask(taskNumber);
	}

	@Override
	public Message execute() {
		if (task == null) {
			int type = Message.TYPE_ERROR;
			String message = String.format(EXECUTE_ERROR);

			return new Message(type, message);
		}

		boolean isSuccess = taskManager.completeTask(task);
		int type;
		String message;
		if (isSuccess) {
			String taskDescription = task.getDescription();
			type = Message.TYPE_SUCCESS;
			message = String.format(EXECUTE_SUCCESS, taskDescription);
		} else {
			type = Message.TYPE_ERROR;
			message = String.format(EXECUTE_ERROR);
		}
		return new Message(type, message);
	}

	@Override
	public Message undo() {
		boolean isSuccess = taskManager.uncompleteTask(task);
		int type;
		String message;
		if (isSuccess) {
			String taskDescription = task.getDescription();
			type = Message.TYPE_SUCCESS;
			message = String.format(UNDO_SUCCESS, taskDescription);
		} else {
			type = Message.TYPE_ERROR;
			message = String.format(UNDO_ERROR);
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