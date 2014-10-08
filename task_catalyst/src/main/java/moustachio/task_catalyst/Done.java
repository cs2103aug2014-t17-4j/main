package moustachio.task_catalyst;

import java.util.Arrays;
import java.util.List;

public class Done extends Action {

	private static final String[] DICTIONARY = { "done", "complete" };
	
	private static final String EXECUTE_ERROR = "There was an error completing the task.";
	private static final String EXECUTE_SUCCESS = "Task successfully completed: %s";
	private static final String UNDO_ERROR = "There was an error restoring the task.";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";
	List<Task> targetList;
	Task task;

	public Done(List<Task> targetList, Task task) {
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

		task.setDone(true);
		boolean isSuccess = task.isDone();
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
		task.setDone(false);
		boolean isSuccess = !task.isDone();
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
	
	@Override
	public boolean isUndoable() {
		return true;
	}
}