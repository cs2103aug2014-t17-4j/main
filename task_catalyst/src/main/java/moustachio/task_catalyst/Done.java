package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Done extends Action {

	private static final String[] DICTIONARY = { "done", "complete" };

	private static final String EXECUTE_SUCCESS = "Task successfully completed: %s";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";

	private static final String EXECUTE_ERROR = "There was/were no matching task(s) to complete.";
	private static final String UNDO_ERROR = "There was an error restoring the task(s).";

	private static final String EXECUTE_SUCCESS_MULTIPLE = "Successfully completed %d tasks.";
	private static final String UNDO_SUCCESS_MULTIPLE = "Successfully restored %d tasks.";

	private static final String HINT_MESSAGE = "Complete: Hit enter after typing the task numbers or keyword."
			+ "\nExamples: done 1 2 3 4, done all, done apple"
			+ "\nAlternatives: done, complete";

	private TaskManager taskManager;
	private ListProcessor listProcessor;
	private List<Task> tasks;

	public Done(String userCommand) {

		taskManager = TaskManagerActual.getInstance();
		listProcessor = new ListProcessorActual();

		String parameter = TaskCatalystCommons.removeFirstWord(userCommand);

		String containsNonNumbers = ".*[^0-9^,^\\s]+.*";
		boolean isContainsWords = parameter.matches(containsNonNumbers);

		if (parameter.equalsIgnoreCase("all")) {
			tasks = new ArrayList<Task>(taskManager.getDisplayList());
		} else if (isContainsWords) {
			List<Task> displayList = taskManager.getDisplayList();
			tasks = listProcessor.searchByKeyword(displayList, parameter);
		} else {
			List<Integer> taskNumbers = TaskCatalystCommons
					.parsePositiveIntList(parameter);

			tasks = new ArrayList<Task>();

			getTasks(taskNumbers);
		}
	}

	@Override
	public Message execute() {

		if (tasks == null) {
			int type = Message.TYPE_ERROR;
			String message = String.format(EXECUTE_ERROR);
			return new Message(type, message);
		}

		int numberCompleted = taskManager.completeTasks(tasks);

		boolean isSingleTask = tasks.size() == 1;
		boolean isCompleted = numberCompleted > 0;
		boolean isAllCompleted = numberCompleted == tasks.size();
		boolean isSuccess = isCompleted && isAllCompleted;

		int type = generateType(isSuccess);
		String message = generateExecuteMessage(isSingleTask, numberCompleted,
				isSuccess);

		return new Message(type, message);
	}

	@Override
	public Message undo() {

		int numberRestored = taskManager.uncompleteTasks(tasks);

		boolean isSingleTask = tasks.size() == 1;
		boolean isRestored = numberRestored > 0;
		boolean isAllRestored = numberRestored == tasks.size();
		boolean isSuccess = isRestored && isAllRestored;

		int type = generateType(isSuccess);
		String message = generateUndoMessage(isSingleTask, numberRestored,
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
			int numberCompleted, boolean isSuccess) {

		String message = String.format(EXECUTE_ERROR);

		if (isSuccess && isSingleTask) {
			Task task = tasks.get(0);
			String taskDescription = task.getDescription();
			message = String.format(EXECUTE_SUCCESS, taskDescription);
		} else if (isSuccess && !isSingleTask) {
			message = String.format(EXECUTE_SUCCESS_MULTIPLE, numberCompleted);
		}

		return message;
	}

	private String generateUndoMessage(boolean isSingleTask,
			int numberRestored, boolean isSuccess) {

		String message = String.format(UNDO_ERROR);

		if (isSuccess && isSingleTask) {
			Task task = tasks.get(0);
			String taskDescription = task.getDescription();
			message = String.format(UNDO_SUCCESS, taskDescription);
		} else if (isSuccess && !isSingleTask) {
			message = String.format(UNDO_SUCCESS_MULTIPLE, numberRestored);
		}

		return message;
	}

	private void getTasks(List<Integer> taskNumbers) {
		for (int taskNumber : taskNumbers) {
			Task displayTask = taskManager.getDisplayTask(taskNumber);
			if (displayTask != null) {
				tasks.add(displayTask);
			}
		}
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