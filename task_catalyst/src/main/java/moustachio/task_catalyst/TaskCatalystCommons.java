package moustachio.task_catalyst;

public class TaskCatalystCommons {

	private static final int INVALID_INTEGER = -1;

	public static int parseInt(String intString) {
		try {
			int value = Integer.parseInt(intString);
			return value;
		} catch (Exception e) {
			return INVALID_INTEGER;
		}
	}

	public static String getFirstWord(String userCommand) {
		String oneOrMoreSpaces = "\\s+";
		String[] splitUserCommand = userCommand.split(oneOrMoreSpaces);
		String firstWord = splitUserCommand[0];
		return firstWord;
	}

	public static String removeFirstWord(String userCommand) {
		String blank = "";
		String firstWord = getFirstWord(userCommand).replaceAll(
				"\\[|\\]|\\\\|\\{|\\}", blank);
		String removedFirstWord = userCommand.replaceFirst(firstWord, blank);
		String removedFirstWordTrimmed = removedFirstWord.trim();
		return removedFirstWordTrimmed;
	}

	public static CommandType getCommandType(String userCommand) {
		if (userCommand == null || userCommand.trim().isEmpty()) {
			return CommandType.INVALID;
		}

		String command = getFirstWord(userCommand);
		String parameters = removeFirstWord(userCommand);
		String commandLowerCase = command.toLowerCase();
		String parametersTrimmed = parameters.trim();

		boolean noParameters = parametersTrimmed.isEmpty();

		if (Hashtag.isThisAction(commandLowerCase) && noParameters) {
			return CommandType.HASHTAG;
		} else if (Delete.isThisAction(commandLowerCase)) {
			return CommandType.DELETE;
		} else if (Done.isThisAction(commandLowerCase)) {
			return CommandType.DONE;
		} else if (Edit.isThisAction(commandLowerCase)) {
			return CommandType.EDIT;
		} else if (Redo.isThisAction(commandLowerCase) && noParameters) {
			return CommandType.REDO;
		} else if (Search.isThisAction(commandLowerCase)) {
			return CommandType.SEARCH;
		} else if (Undo.isThisAction(commandLowerCase) && noParameters) {
			return CommandType.UNDO;
		} else {
			return CommandType.ADD;
		}
	}
}
