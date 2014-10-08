package moustachio.task_catalyst;

import java.util.Arrays;
import java.util.List;

public class Edit extends Action {

	private static final String[] DICTIONARY = { "edit" };

	private static final String EXECUTE_ERROR = "There was an error editing the task.";
	private static final String EXECUTE_SUCCESS = "Task successfully edited: %s";
	private static final String UNDO_ERROR = "There was an error restoring the task.";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";
	List<Task> targetList;
	Task targetTask;
	Task replacementTask;

	public Edit(List<Task> targetList, Task targetTask, Task replacementTask) {
		this.targetList = targetList;
		this.targetTask = targetTask;
		this.replacementTask = replacementTask;
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
		Action delete = new Delete(targetList, targetTask);
		Action add = new Add(targetList, replacementTask);

		boolean isDeleteSuccess = delete.execute().getType() == Message.TYPE_SUCCESS;
		boolean isAddSuccess = add.execute().getType() == Message.TYPE_SUCCESS;
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

	@Override
	public boolean isUndoable() {
		return true;
	}
}