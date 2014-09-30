package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

public class LogicActual implements Logic {

	private static enum CommandType {
		ADD, DELETE, DONE, EDIT, HASHTAG, INVALID, REDO, SEARCH, UNDO
	};

	private static enum DisplayType {
		HASHTAG, SEARCH
	};

	private static final String[] DEFAULT_HASHTAGS = { "#all", "#pri", "#tdy",
			"#tmr", "#upc", "#smd", "#dne" };

	private static final String[] DICTIONARY_DELETE = { "delete", "rm", "del" };
	private static final String[] DICTIONARY_DONE = { "done", "complete" };
	private static final String[] DICTIONARY_EDIT = { "edit" };
	private static final String[] DICTIONARY_REDO = { "redo" };
	private static final String[] DICTIONARY_SEARCH = { "search" };
	private static final String[] DICTIONARY_UNDO = { "undo" };

	private Storage storage;
	private ListProcessor listProcessor;
	private TaskBuilder taskBuilder;

	private List<Task> tasks;
	private List<Task> displayList;

	private DisplayType lastDisplayType;
	private String lastDisplayTerm;

	private Stack<Action> undos;
	private Stack<Action> redos;

	private static final DisplayType DEFAULT_DISPLAY_TYPE = DisplayType.HASHTAG;
	private static final String DEFAULT_DISPLAY_TERM = "all";
	private static final String DEFAULT_FILE_NAME = "tasks.txt";

	private static final int INVALID_INTEGER = -1;

	public LogicActual() {
		lastDisplayType = DEFAULT_DISPLAY_TYPE;
		lastDisplayTerm = DEFAULT_DISPLAY_TERM;

		undos = new Stack<Action>();
		redos = new Stack<Action>();

		storage = new StorageActual();
		listProcessor = new ListProcessorActual();
		taskBuilder = new TaskBuilderAdvanced();

		tasks = storage.loadTasks(DEFAULT_FILE_NAME);
		displayList = tasks;
	}

	// High Level Implementation

	@Override
	public Message processCommand(String userCommand) {
		Action action = generateAction(userCommand);
		Message message = doAction(action);
		save();
		return message;
	}

	private Action generateAction(String userCommand) {
		CommandType COMMAND_TYPE = getCommandType(userCommand);
		Action action = null;
		switch (COMMAND_TYPE) {
		case ADD:
			action = add(userCommand);
			break;
		case DELETE:
			action = delete(userCommand);
			break;
		case DONE:
			action = done(userCommand);
			break;
		case EDIT:
			action = edit(userCommand);
			break;
		case HASHTAG:
			action = hashtag(userCommand);
			break;
		case REDO:
			action = redo();
			break;
		case SEARCH:
			action = search(userCommand);
			break;
		case UNDO:
			action = undo();
			break;
		default:
			action = null;
			break;
		}
		return action;
	}

	private Message doAction(Action action) {
		Message message = new Message(Message.TYPE_ERROR,
				"Type something to begin.");
		if (action != null) {
			message = action.execute();
			boolean isSuccess = message.getType() == Message.TYPE_SUCCESS;
			boolean isUndoable = action.isUndoable();
			if (isSuccess && isUndoable) {
				undos.push(action);
				redos.clear();
			}
			if (isSuccess) {
				refreshList();
			}
		}
		return message;
	}

	private void save() {
		storage.saveTasks(tasks, DEFAULT_FILE_NAME);
	}

	private void refreshList() {
		switch (lastDisplayType) {
		case HASHTAG:
			String hashtag = lastDisplayTerm;
			displayList = listProcessor.searchByHashtag(tasks, hashtag);
			break;
		case SEARCH:
			String keyword = lastDisplayTerm;
			displayList = listProcessor.searchByKeyword(tasks, keyword);
			break;
		}
		displayList = listProcessor.sortByDate(displayList);
	}

	// Operations

	private Action add(String userCommand) {
		Task newTask = taskBuilder.createTask(userCommand);
		Action action = new Add(tasks, newTask);
		return action;
	}

	private Action delete(String userCommand) {
		String taskNumberString = removeFirstWord(userCommand);
		int taskNumber = parseInt(taskNumberString);
		Task deleteTask;
		try {
			deleteTask = displayList.get(taskNumber - 1);
		} catch (Exception e) {
			deleteTask = null;
		}
		Action action = new Delete(tasks, deleteTask);
		return action;
	}

	private Action done(String userCommand) {
		String taskNumberString = removeFirstWord(userCommand);
		int taskNumber = parseInt(taskNumberString);
		Task doneTask;
		try {
			doneTask = displayList.get(taskNumber - 1);
		} catch (Exception e) {
			doneTask = null;
		}
		Action action = new Done(tasks, doneTask);
		return action;
	}

	private Action edit(String userCommand) {
		String taskNumberString = getFirstWord(removeFirstWord(userCommand));
		int taskNumber = parseInt(taskNumberString);
		Task targetTask;
		try {
			targetTask = displayList.get(taskNumber - 1);
		} catch (Exception e) {
			targetTask = null;
		}
		String userInput = removeFirstWord(removeFirstWord(userCommand));
		Task replacementTask = taskBuilder.createTask(userInput);
		Action action = new Edit(tasks, targetTask, replacementTask);
		return action;
	}

	private Action hashtag(String userCommand) {
		String hashtag = getFirstWord(userCommand);
		lastDisplayType = DisplayType.HASHTAG;
		lastDisplayTerm = hashtag;
		return new Hashtag(hashtag);
	}

	private Action redo() {
		return new Redo(tasks, undos, redos);
	}

	private Action search(String userCommand) {
		String keyword = removeFirstWord(userCommand);
		lastDisplayType = DisplayType.SEARCH;
		lastDisplayTerm = keyword;
		return new Search(keyword);
	}

	private Action undo() {
		return new Undo(tasks, undos, redos);
	}

	// API Methods

	@Override
	public List<String> getDefaultHashtags() {
		List<String> defaultHashtagsList = new ArrayList<String>();
		for (String hashtag : DEFAULT_HASHTAGS) {
			defaultHashtagsList.add(hashtag);
		}
		return defaultHashtagsList;
	}

	@Override
	public List<String> getHashtags() {
		SortedSet<String> allHashtagsSet = new TreeSet<String>();
		for (Task task : tasks) {
			List<String> taskHashtags = task.getHashtags();
			allHashtagsSet.addAll(taskHashtags);
		}
		List<String> allHashtagsList = new ArrayList<String>(allHashtagsSet);
		return allHashtagsList;
	}

	@Override
	public Message getMessageTyping(String userCommand) {
		if (userCommand == null || userCommand.trim().isEmpty()) {
			return new Message(Message.TYPE_HINT, "Type something to begin.");
		}

		CommandType COMMAND_TYPE = getCommandType(userCommand);
		int type = Message.TYPE_HINT;
		String message;

		switch (COMMAND_TYPE) {
		case ADD:
			message = getMatchingCommands(userCommand);

			if (!message.isEmpty()) {
				return new Message(Message.TYPE_HINT, "Do you mean " + message
						+ "?");
			} else {
				message = TaskBuilderAdvanced
						.removeCurlyBraces(TaskBuilderAdvanced.removeSquareBrackets(TaskBuilderAdvanced
								.prettyString(TaskBuilderAdvanced
										.interpretedString(userCommand))));
				message += "\nAdd: You can include date information. Use []s to ignore processing.";
				//message = TaskBuilderAdvanced.interpretedString(userCommand);
				// message =
				// "Adding: Use square brackets e.g. [from today to tomorrow] to include date/time information.";
			}
			break;
		case DELETE:
			message = "Delete: Enter the task number to delete. Eqv. commands: delete, rm, del";
			break;
		case DONE:
			message = "Complete: Enter the task number to complete. Eqv. commands: done, complete";
			break;
		case EDIT:
			message = "Edit: Press space or enter after entering a valid task number to continue.";
			String taskNumberString = getFirstWord(removeFirstWord(userCommand));
			boolean endsWithSpace = userCommand.endsWith(" ");
			int taskNumber = parseInt(taskNumberString);
			if (taskNumber > 0) {
				Task editTask;
				try {
					editTask = displayList.get(taskNumber - 1);
				} catch (Exception e) {
					editTask = null;
				}
				boolean isValidTask = editTask != null;
				boolean hasFurtherParameters = !userCommand
						.replaceFirst("edit " + taskNumberString, "").trim()
						.isEmpty();
				if (!isValidTask && (endsWithSpace || hasFurtherParameters)) {
					message = "Edit: Invalid task number specified.";
				} else if (!hasFurtherParameters && endsWithSpace) {
					type = Message.TYPE_AUTOCOMPLETE;
					message = userCommand.trim() + " "
							+ editTask.getDescriptionEdit();
				} else if (hasFurtherParameters && isValidTask) {
					type = Message.TYPE_HINT;
					message = TaskBuilderAdvanced
							.removeCurlyBraces(TaskBuilderAdvanced.removeSquareBrackets(TaskBuilderAdvanced.prettyString(TaskBuilderAdvanced
									.interpretedString(removeFirstWord(removeFirstWord(userCommand))))));
					message += "\nEdit: Hit enter after making your changes.";
				}
			}
			break;
		case HASHTAG:
			message = "Hashtag: Enter a hashtag category to continue.";
			break;
		case REDO:
			message = "Redo: Press enter to redo task.";
			break;
		case SEARCH:
			message = "Search: Enter a keyword to search for (case-insensitive).";
			break;
		case UNDO:
			message = "Undo: Press enter to undo task.";
			break;
		default:
			message = "Type something to begin.";
			break;
		}
		Message returnMessage = new Message(type, message);
		return returnMessage;
	}

	private String getMatchingCommands(String firstWord) {
		if (firstWord.trim().isEmpty()) {
			return "";
		}
		String matchingCommands = "";
		ArrayList<String[]> dictionaries = new ArrayList<String[]>();
		dictionaries.add(DICTIONARY_DELETE);
		dictionaries.add(DICTIONARY_DONE);
		dictionaries.add(DICTIONARY_EDIT);
		dictionaries.add(DICTIONARY_REDO);
		dictionaries.add(DICTIONARY_SEARCH);
		dictionaries.add(DICTIONARY_UNDO);
		for (String[] dictionary : dictionaries) {
			for (String keyword : dictionary) {
				if (!keyword.equalsIgnoreCase(firstWord)
						&& keyword.startsWith(firstWord.toLowerCase())) {
					matchingCommands += "\"" + keyword + "\", ";
				}
			}
		}
		return matchingCommands.replaceAll(", $", "");
	}

	@Override
	public List<Task> getList() {
		return displayList;
	}

	// Parsing Methods

	private int parseInt(String intString) {
		try {
			int value = Integer.parseInt(intString);
			return value;
		} catch (Exception e) {
			return INVALID_INTEGER;
		}
	}

	private String getFirstWord(String userCommand) {
		String oneOrMoreSpaces = "\\s+";
		String[] splitUserCommand = userCommand.split(oneOrMoreSpaces);
		String firstWord = splitUserCommand[0];
		return firstWord;
	}

	private String removeFirstWord(String userCommand) {
		String blank = "";
		String firstWord = getFirstWord(userCommand).replaceAll(
				"\\[|\\]|\\\\|\\{|\\}", blank);
		String removedFirstWord = userCommand.replaceFirst(firstWord, blank);
		String removedFirstWordTrimmed = removedFirstWord.trim();
		return removedFirstWordTrimmed;
	}

	private boolean isFromDictionary(String[] dictionary, String command) {
		boolean isFound = false;
		for (int i = 0; i < dictionary.length; i++) {
			if (dictionary[i].equalsIgnoreCase(command)) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}

	private CommandType getCommandType(String userCommand) {
		if (userCommand == null || userCommand.trim().isEmpty()) {
			return CommandType.INVALID;
		}

		String command = getFirstWord(userCommand);
		String parameters = removeFirstWord(userCommand);
		String commandLowerCase = command.toLowerCase();
		String parametersTrimmed = parameters.trim();

		boolean noParameters = parametersTrimmed.isEmpty();
		boolean hashtagged = commandLowerCase.startsWith("#");
		boolean isHashtag = hashtagged && noParameters;

		if (isHashtag) {
			return CommandType.HASHTAG;
		} else if (isFromDictionary(DICTIONARY_DELETE, commandLowerCase)) {
			return CommandType.DELETE;
		} else if (isFromDictionary(DICTIONARY_DONE, commandLowerCase)) {
			return CommandType.DONE;
		} else if (isFromDictionary(DICTIONARY_EDIT, commandLowerCase)) {
			return CommandType.EDIT;
		} else if (isFromDictionary(DICTIONARY_REDO, commandLowerCase)) {
			return CommandType.REDO;
		} else if (isFromDictionary(DICTIONARY_SEARCH, commandLowerCase)) {
			return CommandType.SEARCH;
		} else if (isFromDictionary(DICTIONARY_UNDO, commandLowerCase)
				&& noParameters) {
			return CommandType.UNDO;
		} else {
			return CommandType.ADD;
		}
	}
}
