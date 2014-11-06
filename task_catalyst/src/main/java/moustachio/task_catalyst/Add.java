package moustachio.task_catalyst;

import java.util.List;

public class Add extends Action {

	private static final String HINT_MESSAGE = "\nAdd: You can include date information. Use []s to ignore processing.";

	private static final String EXECUTE_ERROR = "There was an error adding the task.";
	private static final String EXECUTE_SUCCESS = "Task successfully added: %s";
	private static final String UNDO_ERROR = "There was an error removing the task.";
	private static final String UNDO_SUCCESS = "Task successfully removed: %s";

	private TaskBuilder taskBuilder;
	private TaskManager taskManager;
	private List<Task> tasks;
	private String userCommand;

	public Add(String userCommand) {
		taskBuilder = new TaskBuilderAdvanced();
		taskManager = TaskManagerActual.getInstance();
		tasks = taskBuilder.createTask(userCommand);
		this.userCommand = userCommand;
	}

	@Override
	public Message execute() {
		if (tasks == null) {
			MessageType messageType = MessageType.ERROR;
			String message = String.format(EXECUTE_ERROR) + HINT_MESSAGE;

			return new Message(messageType, message);
		}

		int tasksAdded = taskManager.addTasks(tasks);
		boolean addSuccess = tasksAdded > 0;
		MessageType messageType;
		String message;
		if (addSuccess) {
			String taskDescription = TaskCatalystCommons
					.getFriendlyString(userCommand);
			messageType = MessageType.SUCCESS;
			message = String.format(EXECUTE_SUCCESS, taskDescription);
		} else {
			messageType = MessageType.ERROR;
			message = String.format(EXECUTE_ERROR) + HINT_MESSAGE;
		}
		return new Message(messageType, message);
	}

	@Override
	public Message undo() {
		int tasksRemoved = taskManager.removeTasks(tasks);
		boolean isSuccess = tasksRemoved > 0;
		MessageType messageType;
		String message;
		if (isSuccess) {
			String taskDescription = TaskCatalystCommons
					.getFriendlyString(userCommand);
			messageType = MessageType.SUCCESS;
			message = String.format(UNDO_SUCCESS, taskDescription);
		} else {
			messageType = MessageType.ERROR;
			message = String.format(UNDO_ERROR);
		}
		return new Message(messageType, message);
	}

	public static Message getHint(String userCommand) {

		String messageString;
		MessageType messageType;

		try {
			messageString = TaskCatalystCommons.getFriendlyString(userCommand);
			messageType = MessageType.HINT;
		} catch (UnsupportedOperationException e) {
			messageString = e.getMessage();
			messageType = MessageType.ERROR;
		}
		messageString += HINT_MESSAGE;

		Message returnMessage = new Message(messageType, messageString);

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