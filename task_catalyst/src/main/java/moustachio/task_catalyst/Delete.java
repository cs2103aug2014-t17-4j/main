package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@author A0111890
/**
 * Delete Action relies on the TaskManager for retrieving and deleting tasks. It
 * is able to delete lists of tasks based on search string and task numbers. The
 * user can also use "all" to delete everything.
 */
public class Delete implements Action {
	private static final String[] DICTIONARY = { "delete", "rm", "del" };

	private static final String EXECUTE_ERROR = "There was/were no matching task(s) to delete.";
	private static final String EXECUTE_SUCCESS = "Task successfully deleted: %s";
	private static final String EXECUTE_SUCCESS_MULTIPLE = "Successfully deleted %d tasks.";

	private static final String UNDO_ERROR = "There was an error restoring the task(s).";
	private static final String UNDO_SUCCESS = "Task successfully restored: %s";
	private static final String UNDO_SUCCESS_MULTIPLE = "Successfully restored %d tasks.";

	private static final String HINT_EXAMPLES = "\nExamples: delete 1 2 3 4, delete all, delete apple";
	private static final String HINT_ALTERNATIVES = "\nAlternatives: rm, del, delete";
	private static final String HINT_MESSAGE = "Delete: Hit enter after typing the task numbers or keyword."
			+ HINT_EXAMPLES + HINT_ALTERNATIVES;

	private TaskManager taskManager;
	private ListProcessor listProcessor;
	private List<Task> tasks;

	private String parameters;

	// Initialization Methods

	public Delete(String userCommand) {
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

		int numberRemoved = taskManager.removeTasks(tasks);

		boolean isSingleTask = (tasks.size() == 1);
		boolean isRemoved = (numberRemoved > 0);
		boolean isAllRemoved = (numberRemoved == tasks.size());
		boolean isSuccess = (isRemoved && isAllRemoved);

		MessageType messageType = generateType(isSuccess);
		String message = generateExecuteMessage(isSingleTask, numberRemoved,
				isSuccess);

		Message returnMessage = new Message(messageType, message);

		return returnMessage;
	}

	@Override
	public Message undo() {
		int numberAdded = taskManager.addTasks(tasks);

		boolean isSingleTask = (tasks.size() == 1);
		boolean isAdded = (numberAdded > 0);
		boolean isAllAdded = (numberAdded == tasks.size());
		boolean isSuccess = (isAdded && isAllAdded);

		MessageType messageType = generateType(isSuccess);
		String message = generateUndoMessage(isSingleTask, numberAdded,
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
			int numberRemoved, boolean isSuccess) {
		String message;

		if (isSuccess && isSingleTask) {
			Task task = tasks.get(0);
			String taskDescription = task.getDescription();

			message = String.format(EXECUTE_SUCCESS, taskDescription);
		} else if (isSuccess && !isSingleTask) {
			message = String.format(EXECUTE_SUCCESS_MULTIPLE, numberRemoved);
		} else {
			message = String.format(EXECUTE_ERROR);
		}

		return message;
	}

	private String generateUndoMessage(boolean isSingleTask, int numberAdded,
			boolean isSuccess) {
		String message;

		if (isSuccess && isSingleTask) {
			Task task = tasks.get(0);
			String taskDescription = task.getDescription();

			message = String.format(UNDO_SUCCESS, taskDescription);
		} else if (isSuccess && !isSingleTask) {
			message = String.format(UNDO_SUCCESS_MULTIPLE, numberAdded);
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