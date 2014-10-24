package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Delete extends Action {

	private static final String[] DICTIONARY = { "delete", "rm", "del" };

	private static final String EXECUTE_SUCCESS = "Task successfully deleted: %s";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";

	private static final String EXECUTE_ERROR = "There was an error deleting the task(s).";
	private static final String UNDO_ERROR = "There was an error restoring the task(s).";

	private static final String EXECUTE_SUCCESS_MULTIPLE = "Successfully deleted %d tasks.";
	private static final String UNDO_SUCCESS_MULTIPLE = "Successfully restored %d tasks.";

	private static final String HINT_MESSAGE = "Delete: Enter the task number to delete. Eqv. commands: delete, rm, del";

	private TaskManager taskManager;
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
			tasks = new ArrayList<Task>();
			for (int taskNumber : taskNumbers) {
				Task displayTask = taskManager.getDisplayTask(taskNumber);
				if (displayTask != null) {
					tasks.add(displayTask);
				}
			}
		}
	}

	@Override
	public Message execute() {

		if (tasks == null) {
			int type = Message.TYPE_ERROR;
			String message = String.format(EXECUTE_ERROR);
			return new Message(type, message);
		}

		int numberRemoved = taskManager.removeTasks(tasks);

		boolean isSingleTask = tasks.size() == 1;
		boolean isRemoved = numberRemoved > 0;
		boolean isAllRemoved = numberRemoved == tasks.size();
		boolean isSuccess = isRemoved && isAllRemoved;

		int type = generateType(isSuccess);
		String message = generateExecuteMessage(isSingleTask, numberRemoved,
				isSuccess);

		return new Message(type, message);
	}

	@Override
	public Message undo() {

		int numberAdded = taskManager.addTasks(tasks);

		boolean isSingleTask = tasks.size() == 1;
		boolean isAdded = numberAdded > 0;
		boolean isAllAdded = numberAdded == tasks.size();
		boolean isSuccess = isAdded && isAllAdded;

		int type = generateType(isSuccess);
		String message = generateUndoMessage(isSingleTask, numberAdded,
				isSuccess);

		return new Message(type, message);
	}

	private int generateType(boolean isSuccess) {
		int type;
		if (isSuccess) {
			type = Message.TYPE_SUCCESS;
		} else {
			type = Message.TYPE_ERROR;
		}
		return type;
	}

	private String generateExecuteMessage(boolean isSingleTask,
			int numberRemoved, boolean isSuccess) {

		String message = String.format(EXECUTE_ERROR);

		if (isSuccess && isSingleTask) {
			Task task = tasks.get(0);
			String taskDescription = task.getDescription();
			message = String.format(EXECUTE_SUCCESS, taskDescription);
		} else if (isSuccess && !isSingleTask) {
			message = String.format(EXECUTE_SUCCESS_MULTIPLE, numberRemoved);
		}

		return message;
	}

	private String generateUndoMessage(boolean isSingleTask, int numberAdded,
			boolean isSuccess) {

		String message = String.format(UNDO_ERROR);

		if (isSuccess && isSingleTask) {
			Task task = tasks.get(0);
			String taskDescription = task.getDescription();
			message = String.format(UNDO_SUCCESS, taskDescription);
		} else if (isSuccess && !isSingleTask) {
			message = String.format(UNDO_SUCCESS_MULTIPLE, numberAdded);
		}

		return message;
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