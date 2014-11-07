package moustachio.task_catalyst;

import java.util.List;

// @author A0111890
public class Add extends Action {
	private static final String EXECUTE_SUCCESS = "Task successfully added: %s";
	private static final String EXECUTE_ERROR = "There was an error adding the task.";

	private static final String UNDO_SUCCESS = "Task successfully removed: %s";
	private static final String UNDO_ERROR = "There was an error removing the task.";

	private static final String HINT_MESSAGE = "\nAdd: You can include date information. Use []s to ignore processing.";

	private TaskBuilder taskBuilder;
	private TaskManager taskManager;
	private List<Task> tasks;
	private String userCommand;

	public Add(String userCommand) {
		this.taskBuilder = new TaskBuilderAdvanced();
		this.taskManager = TaskManagerActual.getInstance();
		this.tasks = this.taskBuilder.createTask(userCommand);
		this.userCommand = userCommand;
	}

	@Override
	public Message execute() {
		MessageType messageType;
		String message;

		boolean isNullTask = (this.tasks == null);

		if (isNullTask) {
			messageType = MessageType.ERROR;
			message = String.format(EXECUTE_ERROR);
			message = message + HINT_MESSAGE;

			return new Message(messageType, message);
		}

		int tasksAdded = this.taskManager.addTasks(this.tasks);

		boolean isSuccess = tasksAdded > 0;

		if (isSuccess) {
			String taskDescription = TaskCatalystCommons
					.getFriendlyString(this.userCommand);
			messageType = MessageType.SUCCESS;
			message = String.format(EXECUTE_SUCCESS, taskDescription);
		} else {
			messageType = MessageType.ERROR;
			message = String.format(EXECUTE_ERROR);
			message = message + HINT_MESSAGE;
		}

		return new Message(messageType, message);
	}

	@Override
	public Message undo() {
		MessageType messageType;
		String message;

		int tasksRemoved = this.taskManager.removeTasks(this.tasks);

		boolean isSuccess = tasksRemoved > 0;

		if (isSuccess) {
			String taskDescription = TaskCatalystCommons
					.getFriendlyString(this.userCommand);
			messageType = MessageType.SUCCESS;
			message = String.format(UNDO_SUCCESS, taskDescription);
		} else {
			messageType = MessageType.ERROR;
			message = String.format(UNDO_ERROR);
		}

		return new Message(messageType, message);
	}

	public static Message getHint(String userCommand) {
		MessageType messageType;
		String message;

		try {
			message = TaskCatalystCommons.getFriendlyString(userCommand);
			messageType = MessageType.HINT;
		} catch (UnsupportedOperationException e) {
			message = e.getMessage();
			messageType = MessageType.ERROR;
		}

		message = message + HINT_MESSAGE;

		Message returnMessage = new Message(messageType, message);

		return returnMessage;
	}

	public static boolean isThisAction(String command) {
		return true;
	}

	@Override
	public boolean isUndoable() {
		return true;
	}
}