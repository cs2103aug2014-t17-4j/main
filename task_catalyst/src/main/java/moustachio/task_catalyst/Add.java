package moustachio.task_catalyst;

public class Add extends Action {

	private static final String EXECUTE_ERROR = "There was an error adding the task.";
	private static final String EXECUTE_SUCCESS = "Task successfully added: %s";
	private static final String UNDO_ERROR = "There was an error removing the task.";
	private static final String UNDO_SUCCESS = "Task successfully removed: %s";

	private TaskBuilder taskBuilder;
	private TaskManager taskManager;
	private Task task;

	public Add(String userCommand) {
		taskBuilder = new TaskBuilderAdvanced();
		taskManager = TaskManagerActual.getInstance();
		task = taskBuilder.createTask(userCommand);
	}

	@Override
	public Message execute() {
		if (task == null) {
			int type = Message.TYPE_ERROR;
			String message = String.format(EXECUTE_ERROR);

			return new Message(type, message);
		}

		boolean addSuccess = taskManager.addTask(task);
		int type;
		String message;
		if (addSuccess) {
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
		boolean isSuccess = taskManager.removeTask(task);
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

	public static Message getHint(String userCommand) {

		String messageString;
		int messageType;

		try {
			messageString = TaskCatalystCommons.getFriendlyString(userCommand);
			messageType = Message.TYPE_HINT;
		} catch (UnsupportedOperationException e) {
			messageString = "You cannot mix date types, and you can only specify one pair of date ranges per task.";
			messageType = Message.TYPE_ERROR;
		}
		messageString += "\nAdd: You can include date information. Use []s to ignore processing.";

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