package moustachio;

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

	private static final String[] HASHTAGS_DEFAULT = { "#all", "#pri", "#tdy",
			"#tmr", "#upc", "#smd", "#dne" };
	private static final String[] DICTIONARY_DELETE = { "delete", "rm", "del" };
	private static final String[] DICTIONARY_DISPLAY = { "display", "ls", "dir" };
	private static final String[] DICTIONARY_EXIT = { "exit", "quit" };
	private static final String[] DICTIONARY_REDO = { "redo" };
	private static final String[] DICTIONARY_SEARCH = { "search" };
	private static final String[] DICTIONARY_UNDO = { "undo" };

	private Storage storage;
	private ListProcessor listProcessor;
	private List<Task> tasks;
	private List<Task> displayList;
	private Stack<Action> undos;
	private Stack<Action> redos;

	private DisplayType lastDisplayType;
	private String lastDisplayTerm;

	public LogicActual() {
		lastDisplayType = DisplayType.HASHTAG;
		lastDisplayTerm = "all";

		undos = new Stack<Action>();
		redos = new Stack<Action>();

		storage = new StorageStub();
		listProcessor = new ListProcessorStub();

		tasks = storage.loadTasks("tasks.txt");
	}

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
			break;
		case DISPLAY:
			break;
		case HASHTAG:
			break;
		case REDO:
			break;
		case SEARCH:
			break;
		case UNDO:
			break;
		case EXIT:
			break;
		default:
			break;
		}
		return action;
	}

	private void save() {
		storage.saveTasks(tasks, "tasks.txt");
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

	private Message doAction(Action action) {
		Message message = new Message(Message.TYPE_ERROR, "Invalid Action Encountered");
		if (action != null) {
			message = action.execute();
			undos.push(action);
			redos.clear();
		}
		return message;
	}

	private Action add(String userCommand) {
		Action action;
		Task newTask = TaskBuilder.createTask(userCommand);
		action = new Add(tasks, newTask);
		return action;
	}

	@Override
	public List<String> getDefaultHashtags() {
		List<String> defaultHashtagsList = new ArrayList<String>();
		for (String hashtag : HASHTAGS_DEFAULT) {
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

	// Parsing Methods

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

		if (commandLowerCase.startsWith("#") && parametersTrimmed.isEmpty()) {
			return CommandType.HASHTAG;
		} else if (isFromDictionary(DICTIONARY_DELETE, commandLowerCase)) {
			return CommandType.DELETE;
		} else if (isFromDictionary(DICTIONARY_DISPLAY, commandLowerCase)) {
			return CommandType.DISPLAY;
		} else if (isFromDictionary(DICTIONARY_EXIT, commandLowerCase)) {
			return CommandType.EXIT;
		} else if (isFromDictionary(DICTIONARY_REDO, commandLowerCase)) {
			return CommandType.REDO;
		} else if (isFromDictionary(DICTIONARY_SEARCH, commandLowerCase)) {
			return CommandType.SEARCH;
		} else if (isFromDictionary(DICTIONARY_UNDO, commandLowerCase)) {
			return CommandType.UNDO;
		} else {
			return CommandType.ADD;
		}
	}

	@Override
	public List<Task> getList() {
		return displayList;
	}
}
