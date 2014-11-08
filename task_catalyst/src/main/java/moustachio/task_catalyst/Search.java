package moustachio.task_catalyst;

import java.util.Arrays;
import java.util.List;

//@author A0111890L
/**
 * Search Action parses the user's input and sets the TaskManager to use the
 * specified hashtag and the correct display mode.
 */
public class Search implements Action {
	private static final String[] DICTIONARY = { "search", "find" };

	private static final String EXECUTE_SUCCESS = "Displaying search: %s.";
	private static final String EXECUTE_ERROR = "Please enter a valid search term.";

	private static final String HINT_MESSAGE = "Search: Hit enter after typing a search term to continue."
			+ "\nSyntax: search <search term>";

	TaskManager taskManager;
	String keyword;

	public Search(String userCommand) {
		this.taskManager = TaskManagerActual.getInstance();
		this.keyword = TaskCatalystCommons.removeFirstWord(userCommand);
	}

	@Override
	public Message execute() {
		MessageType messageType;
		String message;

		boolean isKeywordEmpty = this.keyword.isEmpty();

		if (isKeywordEmpty) {
			messageType = MessageType.ERROR;
			message = String.format(EXECUTE_ERROR);
		} else {
			DisplayMode displayMode = DisplayMode.SEARCH;
			this.taskManager.setDisplayModeKeyword(displayMode, this.keyword);

			messageType = MessageType.SUCCESS;
			message = String.format(EXECUTE_SUCCESS, this.keyword);
		}

		Message returnMessage = new Message(messageType, message);

		return returnMessage;
	}

	@Override
	public Message undo() {
		Message returnMessage = execute();

		return returnMessage;
	}

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
		return false;
	}
}