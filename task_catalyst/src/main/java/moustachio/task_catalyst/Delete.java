package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Delete extends Action {

	private static final String[] DICTIONARY = { "delete", "rm", "del" };

	private static final String EXECUTE_ERROR = "There was an error deleting the task.";
	private static final String EXECUTE_SUCCESS = "Task successfully deleted: %s";
	private static final String UNDO_ERROR = "There was an error restoring the task.";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";

	private static final String EXECUTE_ERROR_MULTIPLE = "There is nothing to delete.";
	private static final String EXECUTE_SUCCESS_MULTIPLE = "Successfully deleted %d tasks.";
	private static final String UNDO_ERROR_MULTIPLE = "There was an error restoring the tasks.";
	private static final String UNDO_SUCCESS_MULTIPLE = "Successfully restored %d tasks.";

	private static final String HINT_MESSAGE = "Delete: Enter the task number to delete. Eqv. commands: delete, rm, del";

	private TaskManager taskManager;
	private Task task;
	private List<Task> tasks;

	public Delete(String userCommand) {
		taskManager = TaskManagerActual.getInstance();
		String taskNumberString = TaskCatalystCommons
				.removeFirstWord(userCommand);
		if (taskNumberString.equalsIgnoreCase("all")) {
			tasks = new ArrayList<Task>(taskManager.getList());
		} else {
			List<Integer> taskNumbers = TaskCatalystCommons
					.parsePositiveIntList(taskNumberString);
			if (taskNumbers.size() > 1) {
				tasks = new ArrayList<Task>();
				for (int taskNumber : taskNumbers) {
					Task displayTask = taskManager.getDisplayTask(taskNumber);
					if (displayTask != null) {
						tasks.add(displayTask);
					}
				}
			} else {
				int taskNumber = TaskCatalystCommons
						.parsePositiveInt(taskNumberString);
				task = taskManager.getDisplayTask(taskNumber);
			}
		}
	}

	@Override
	public Message execute() {
		if (task == null && tasks == null) {
			int type = Message.TYPE_ERROR;
			String message = String.format(EXECUTE_ERROR);

			return new Message(type, message);
		}

		int type;
		String message;
		if (task != null) {
			boolean isSuccess = taskManager.removeTask(task);
			if (isSuccess) {
				String taskDescription = task.getDescription();
				type = Message.TYPE_SUCCESS;
				message = String.format(EXECUTE_SUCCESS, taskDescription);
			} else {
				type = Message.TYPE_ERROR;
				message = String.format(EXECUTE_ERROR);
			}
		} else {
			int numberRemoved = taskManager.removeTasks(tasks);
			boolean isSuccess = numberRemoved > 0;
			if (isSuccess) {
				type = Message.TYPE_SUCCESS;
				message = String
						.format(EXECUTE_SUCCESS_MULTIPLE, numberRemoved);
			} else {
				type = Message.TYPE_ERROR;
				message = String.format(EXECUTE_ERROR_MULTIPLE);
			}
		}
		return new Message(type, message);
	}

	@Override
	public Message undo() {
		int type;
		String message;
		if (task != null) {
			boolean isSuccess = taskManager.addTask(task);
			if (isSuccess) {
				String taskDescription = task.getDescription();
				type = Message.TYPE_SUCCESS;
				message = String.format(UNDO_SUCCESS, taskDescription);
			} else {
				type = Message.TYPE_ERROR;
				message = String.format(UNDO_ERROR);
			}
		} else {
			int numberAdded = taskManager.addTasks(tasks);
			boolean isSuccess = numberAdded > 0;
			if (isSuccess) {
				type = Message.TYPE_SUCCESS;
				message = String.format(UNDO_SUCCESS_MULTIPLE, numberAdded);
			} else {
				type = Message.TYPE_ERROR;
				message = String.format(UNDO_ERROR_MULTIPLE);
			}
		}
		return new Message(type, message);
	}

	public static Message getHint(String userCommand) {
		int type = Message.TYPE_HINT;
		Message returnMessage = new Message(type, HINT_MESSAGE);
		return returnMessage;
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