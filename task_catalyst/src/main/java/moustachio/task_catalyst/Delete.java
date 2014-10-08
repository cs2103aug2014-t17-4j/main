package moustachio.task_catalyst;

import java.util.Arrays;
import java.util.List;

public class Delete extends Action {

	private static final String[] DICTIONARY = { "delete", "rm", "del" };

	private static final String EXECUTE_ERROR = "There was an error deleting the task.";
	private static final String EXECUTE_SUCCESS = "Task successfully deleted: %s";
	private static final String UNDO_ERROR = "There was an error restoring the task.";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";
	List<Task> targetList;
	Task task;

	public Delete(List<Task> targetList, Task task) {
		this.targetList = targetList;
		this.task = task;
	}

	@Override
	public Message execute() {
		if (task == null) {
			int type = Message.TYPE_ERROR;
			String message = String.format(EXECUTE_ERROR);

			return new Message(type, message);
		}

		boolean isSuccess = targetList.remove(task);
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
		boolean isSuccess = targetList.add(task);
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