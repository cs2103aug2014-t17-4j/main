package moustachio.task_catalyst;

import java.util.List;

public class Add extends Action {

	private static final String EXECUTE_ERROR = "There was an error adding the task.";
	private static final String EXECUTE_SUCCESS = "Task successfully added: %s";
	private static final String UNDO_ERROR = "There was an error removing the task.";
	private static final String UNDO_SUCCESS = "Task successfully removed: %s";
	List<Task> targetList;
	Task task;

	public Add(List<Task> targetList, Task task) {
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

		boolean addSuccess = targetList.add(task);
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
		boolean isSuccess = targetList.remove(task);
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

	@Override
	public boolean isUndoable() {
		return true;
	}
}