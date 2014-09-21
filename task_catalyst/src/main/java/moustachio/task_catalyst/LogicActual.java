package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

public class LogicActual implements Logic {

	private static enum CommandType {
		ADD, DELETE, DISPLAY, EXIT, HASHTAG, REDO, SEARCH, UNDO
	};

	private static enum DisplayType {
		HASHTAG, SEARCH
	};

	private static final String[] DEFAULT_HASHTAGS = { "#all", "#pri", "#tdy",
			"#tmr", "#upc", "#smd", "#dne" };

	private static final String[] DICTIONARY_DELETE = { "delete", "rm", "del" };
	private static final String[] DICTIONARY_REDO = { "redo" };
	private static final String[] DICTIONARY_SEARCH = { "search" };
	private static final String[] DICTIONARY_UNDO = { "undo" };

	private Storage storage;
	private ListProcessor listProcessor;

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

		storage = new StorageStub();
		listProcessor = new ListProcessorStub();

		tasks = storage.loadTasks(DEFAULT_FILE_NAME);
	}

	// High Level Implementation

	@Override
	public Message processCommand(String userCommand) {
		Action action = generateAction(userCommand);
		Message message = doAction(action);
		save();
		refreshList();
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
			throw new Error();
		}
		return action;
	}

	private Message doAction(Action action) {
		Message message = new Message(Message.TYPE_ERROR,
				"Invalid Action Encountered");
		if (action != null) {
			message = action.execute();
			boolean isSuccess = message.getType() == Message.TYPE_SUCCESS;
			boolean isUndoable = action.isUndoable();
			if (isSuccess && isUndoable) {
				undos.push(action);
				redos.clear();
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
	}

	// Operations

	private Action add(String userCommand) {
		Task newTask = TaskBuilder.createTask(userCommand);
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

	private Action hashtag(String userCommand) {
		String hashtag = removeFirstWord(userCommand);
		lastDisplayType = DisplayType.HASHTAG;
		lastDisplayTerm = hashtag;
		return null;
	}

	private Action redo() {
		return new Redo(displayList, undos, redos);
	}

	private Action search(String userCommand) {
		String keyword = removeFirstWord(userCommand);
		lastDisplayType = DisplayType.SEARCH;
		lastDisplayTerm = keyword;
		return null;
	}

	private Action undo() {
		return new Undo(displayList, undos, redos);
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
			List<String> taskHashtags = task.getHashTags();
			allHashtagsSet.addAll(taskHashtags);
		}
		List<String> allHashtagsList = new ArrayList<String>(allHashtagsSet);
		return allHashtagsList;
	}

	@Override
	public Message getMessageTyping(String userCommand) {
		CommandType COMMAND_TYPE = getCommandType(userCommand);
		switch (COMMAND_TYPE) {
		default:
			return new Message(Message.TYPE_SUCCESS, userCommand);
		}
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
		String firstWord = getFirstWord(userCommand);
		String removedFirstWord = userCommand.replaceFirst(firstWord, blank);
		String removedFirstWordTrimmed = removedFirstWord.trim();
		return removedFirstWordTrimmed;
	}

	private boolean isFromDictionary(String[] dictionary, String command) {
		boolean isFound = false;
		for (int i = 0; i < dictionary.length; i++) {
			if (dictionary[i].equals(command)) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}

	private CommandType getCommandType(String userCommand) {
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
		} else if (isFromDictionary(DICTIONARY_REDO, commandLowerCase)
				&& noParameters) {
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
