package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@author A0111890L
/**
 * Done Action relies on the TaskManager for retrieving and completing tasks. It
 * is able to complete lists of tasks based on search string and task numbers.
 * The user can also use "all" to complete everything.
 */
public class Done implements Action {
	private static final String[] DICTIONARY = { "done", "complete" };

	private static final String EXECUTE_ERROR = "There was/were no matching task(s) to complete.";
	private static final String EXECUTE_SUCCESS = "Task successfully completed: %s";
	private static final String EXECUTE_SUCCESS_MULTIPLE = "Successfully completed %d tasks.";

	private static final String UNDO_ERROR = "There was an error restoring the task(s).";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";
	private static final String UNDO_SUCCESS_MULTIPLE = "Successfully restored %d tasks.";

	private static final String HINT_EXAMPLES = "\nExamples: done 1 2 3 4, done all, done apple";
	private static final String HINT_ALTERNATIVES = "\nAlternatives: done, complete";
	private static final String HINT_MESSAGE = "Complete: Hit enter after typing the task numbers or keyword."
			+ HINT_EXAMPLES + HINT_ALTERNATIVES;

	private TaskManager taskManager;
	private ListProcessor listProcessor;
	private List<Task> tasks;

	private String parameters;

	// Initialization Methods

	public Done(String userCommand) {
		initializeComponents();
		initializeParameters(userCommand);
		populateTasks();
	}

	private void initializeComponents() {
		taskManager = TaskManagerActual.getInstance();
		listProcessor = new ListProcessorActual();
	}

	private void initializeParameters(String userCommand) {
		parameters = TaskCatalystCommons.removeFirstWord(userCommand);
	}

	private void populateTasks() {
		String containsNonNumbers = ".*[^0-9^,^\\s]+.*";

		boolean isContainsWords = parameters.matches(containsNonNumbers);
		boolean isAllSpecified = parameters.equalsIgnoreCase("all");

		if (isAllSpecified) {
			tasks = getAllTasks();
		} else if (isContainsWords) {
			tasks = getTaskByKeywords(parameters);
		} else {
			tasks = getTasksByNumber(parameters);
		}
	}

	private ArrayList<Task> getAllTasks() {
		return new ArrayList<Task>(taskManager.getDisplayList());
	}

	private List<Task> getTasksByNumber(String parameters) {
		List<Integer> taskNumbers = TaskCatalystCommons
				.parsePositiveIntList(parameters);

		List<Task> foundTasks = new ArrayList<Task>();

		for (int taskNumber : taskNumbers) {
			Task displayTask = taskManager.getDisplayTask(taskNumber);

			if (displayTask != null) {
				foundTasks.add(displayTask);
			}
		}

		return foundTasks;
	}

	private List<Task> getTaskByKeywords(String parameter) {
		List<Task> displayList = taskManager.getDisplayList();

		return listProcessor.searchByKeyword(displayList, parameter);
	}

	// Execution/Undo Methods

	@Override
	public Message execute() {
		boolean isTasksFound = (tasks != null);

		if (!isTasksFound) {
			MessageType messageType = MessageType.ERROR;
			String message = String.format(EXECUTE_ERROR);

			Message returnMessage = new Message(messageType, message);

			return returnMessage;
		}

		int numberCompleted = taskManager.completeTasks(tasks);

		boolean isSingleTask = tasks.size() == 1;
		boolean isCompleted = numberCompleted > 0;
		boolean isAllCompleted = numberCompleted == tasks.size();
		boolean isSuccess = isCompleted && isAllCompleted;

		MessageType messageType = generateType(isSuccess);
		String message = generateExecuteMessage(isSingleTask, numberCompleted,
				isSuccess);

		Message returnMessage = new Message(messageType, message);

		return returnMessage;
	}

	@Override
	public Message undo() {
		int numberRestored = taskManager.uncompleteTasks(tasks);

		boolean isSingleTask = (tasks.size() == 1);
		boolean isRestored = (numberRestored > 0);
		boolean isAllRestored = (numberRestored == tasks.size());
		boolean isSuccess = (isRestored && isAllRestored);

		MessageType messageType = generateType(isSuccess);
		String message = generateUndoMessage(isSingleTask, numberRestored,
				isSuccess);

		Message returnMessage = new Message(messageType, message);

		return returnMessage;
	}

	private MessageType generateType(boolean isSuccess) {
		MessageType messageType;

		if (isSuccess) {
			messageType = MessageType.SUCCESS;
		} else {
			messageType = MessageType.ERROR;
		}

		return messageType;
	}

	private String generateExecuteMessage(boolean isSingleTask,
			int numberCompleted, boolean isSuccess) {
		String message;

		if (isSuccess && isSingleTask) {
			Task task = tasks.get(0);
			String taskDescription = task.getDescription();

			message = String.format(EXECUTE_SUCCESS, taskDescription);
		} else if (isSuccess && !isSingleTask) {
			message = String.format(EXECUTE_SUCCESS_MULTIPLE, numberCompleted);
		} else {
			message = String.format(EXECUTE_ERROR);
		}

		return message;
	}

	private String generateUndoMessage(boolean isSingleTask,
			int numberRestored, boolean isSuccess) {
		String message;

		if (isSuccess && isSingleTask) {
			Task task = tasks.get(0);
			String taskDescription = task.getDescription();

			message = String.format(UNDO_SUCCESS, taskDescription);
		} else if (isSuccess && !isSingleTask) {
			message = String.format(UNDO_SUCCESS_MULTIPLE, numberRestored);
		} else {
			message = String.format(UNDO_ERROR);
		}

		return message;
	}

	// Other Methods

	public static Message getHint(String userCommand) {
		MessageType messageType = MessageType.HINT;

		Message returnMessage = new Message(messageType, HINT_MESSAGE);

		return returnMessage;
	}

	public static boolean isThisAction(String command) {
		List<String> dictionaryList = Arrays.asList(DICTIONARY);

		boolean isContainsCommand = dictionaryList.contains(command);

		return isContainsCommand;
	}

	public static String[] getDictionary() {
		return DICTIONARY;
	}

	@Override
	public boolean isUndoable() {
		return true;
	}
}