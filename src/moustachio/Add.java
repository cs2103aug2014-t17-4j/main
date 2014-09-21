package moustachio;

import java.util.List;

public class Add extends UndoableAction {

	private static final String EXECUTE_SUCCESS = "There was an error adding the task: %s";
	private static final String EXECUTE_ERROR = "Task successfully added: %s";
	private static final String UNDO_ERROR = "(Undo) There was an error removing the task: %s";
	private static final String UNDO_SUCCESS = "(Undo) Task successfully removed: %s";
	List<Task> targetList;
	Task task;

	public Add(List<Task> targetList, Task task) {
		this.targetList = targetList;
		this.task = task;
	}

	@Override
	public Message execute() {
		boolean addSuccess = targetList.add(task);
		String taskDescription = task.getDescription();
		int type;
		String message;
		if (addSuccess) {
			type = Message.TYPE_SUCCESS;
			message = String.format(EXECUTE_SUCCESS, taskDescription);
		} else {
			type = Message.TYPE_ERROR;
			message = String.format(EXECUTE_ERROR, taskDescription);
		}
		return new Message(type, message);
	}

	@Override
	public Message undo() {
		boolean removeSuccess = targetList.remove(task);
		String taskDescription = task.getDescription();
		int type;
		String message;
		if (removeSuccess) {
			type = Message.TYPE_SUCCESS;
			message = String.format(UNDO_SUCCESS, taskDescription);
		} else {
			type = Message.TYPE_ERROR;
			message = String.format(UNDO_ERROR, taskDescription);
		}
		return new Message(type, message);
	}

}