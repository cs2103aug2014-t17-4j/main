package moustachio.task_catalyst;

import java.util.List;

public class Edit extends Action {

	private static final String EXECUTE_SUCCESS = "There was an error editing the task.";
	private static final String EXECUTE_ERROR = "Task successfully edited: %s";
	private static final String UNDO_ERROR = "There was an error restoring the task.";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";
	List<Task> targetList;
	Task task;
	Task edited;

	public Edit(List<Task> targetList, Task task, Task edited) {
		this.targetList = targetList;
		this.task = task;
		this.task = edited;
	}

	@Override
	public Message execute() {
		return replace(task, edited, EXECUTE_SUCCESS, EXECUTE_ERROR);
	}

	@Override
	public Message undo() {
		return replace(edited, task, UNDO_SUCCESS, UNDO_ERROR);
	}

	private Message replace(Task target, Task replacement,
			String successFormat, String errorFormat) {
		Action delete = new Delete(targetList, target);
		Action add = new Add(targetList, replacement);

		boolean isDeleteSuccess = delete.execute().getType() == Message.TYPE_SUCCESS;
		boolean isAddSuccess = add.execute().getType() == Message.TYPE_SUCCESS;
		int type;
		String message;
		if (isDeleteSuccess && isAddSuccess) {
			type = Message.TYPE_SUCCESS;
			message = String.format(successFormat, edited.getDescription());
		} else {
			type = Message.TYPE_ERROR;
			message = String.format(errorFormat);
		}
		return new Message(type, message);
	}

	@Override
	public boolean isUndoable() {
		return true;
	}
}