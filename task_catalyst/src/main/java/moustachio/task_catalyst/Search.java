package moustachio.task_catalyst;

import java.util.Arrays;

public class Search extends Action {

	private static final String[] DICTIONARY = { "search" };

	private static final String EXECUTE_SUCCESS = "Displaying search: %s.";
	private static final String EXECUTE_ERROR = "Please enter a valid search term.";

	private static final String HINT_MESSAGE = "Search: Hit enter after typing a search term to continue."
			+ "\nSyntax: search <search term>";

	TaskManager taskManager;
	String keyword;

	public Search(String userCommand) {
		taskManager = TaskManagerActual.getInstance();
		keyword = TaskCatalystCommons.removeFirstWord(userCommand);
	}

	@Override
	public Message execute() {
		int type;
		String message;
		if (keyword.isEmpty()) {
			type = Message.TYPE_ERROR;
			message = String.format(EXECUTE_ERROR);
		} else {
			taskManager.setDisplayMode(DisplayMode.SEARCH);
			taskManager.setDisplayKeyword(keyword);
			type = Message.TYPE_SUCCESS;
			message = String.format(EXECUTE_SUCCESS, keyword);
		}
		return new Message(type, message);
	}

	@Override
	public Message undo() {
		return execute();
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
		return false;
	}
}