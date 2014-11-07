package moustachio.task_catalyst;

import java.util.ArrayList;

// @author A0111890
public class ActionHintSystemActual implements ActionHintSystem {
	private static final String ERROR_ACTION_UNDEFINED = "Undefined Action Encountered.";
	private static final String FORMAT_PARTIAL_SUGGESTIONS = "Do you mean %s?";
	private static final String HINT_MESSAGE_DEFAULT = "Type something to begin adding a task."
			+ "\nOther Commands: delete, edit, done, redo, undo, #, find. Press CTRL+H for more details.";

	ActionInvoker actionInvoker;

	public ActionHintSystemActual() {
		actionInvoker = ActionInvoker.getInstance();
		actionInvoker.setDefaultMessage(HINT_MESSAGE_DEFAULT);
	}

	public void testMode() {
		actionInvoker.testMode();
	}

	// High Level Methods

	@Override
	public Message processCommand(String userCommand) {
		Action action = generateAction(userCommand);

		Message message = actionInvoker.doAction(action);

		return message;
	}

	@Override
	public Message getMessageTyping(String userCommand) {
		boolean isInvalidCommand = isInvalidCommand(userCommand);

		if (isInvalidCommand) {
			return new Message(MessageType.HINT, HINT_MESSAGE_DEFAULT);
		}

		CommandType COMMAND_TYPE = TaskCatalystCommons
				.getCommandType(userCommand);

		Message message;

		switch (COMMAND_TYPE) {
		case ADD:
			message = getHintPartialAdd(userCommand);
			break;

		case DELETE:
			message = Delete.getHint(userCommand);
			break;

		case DONE:
			message = Done.getHint(userCommand);
			break;

		case EDIT:
			message = Edit.getHint(userCommand);
			break;

		case HASHTAG:
			message = Hashtag.getHint(userCommand);
			break;

		case REDO:
			message = Redo.getHint(userCommand);
			break;

		case SEARCH:
			message = Search.getHint(userCommand);
			break;

		case UNDO:
			message = Undo.getHint(userCommand);
			break;

		case UNDONE:
			message = Undone.getHint(userCommand);
			break;

		default:
			message = getHintDefault();
			break;
		}

		return message;
	}

	private Action generateAction(String userCommand) {
		CommandType COMMAND_TYPE = TaskCatalystCommons
				.getCommandType(userCommand);

		Action action = null;

		switch (COMMAND_TYPE) {
		case ADD:
			action = new Add(userCommand);
			break;

		case DELETE:
			action = new Delete(userCommand);
			break;

		case DONE:
			action = new Done(userCommand);
			break;

		case EDIT:
			action = new Edit(userCommand);
			break;

		case HASHTAG:
			action = new Hashtag(userCommand);
			break;

		case REDO:
			action = new Redo(userCommand);
			break;

		case SEARCH:
			action = new Search(userCommand);
			break;

		case UNDO:
			action = new Undo(userCommand);
			break;

		case UNDONE:
			action = new Undone(userCommand);
			break;

		case INVALID:
			action = null;
			break;

		default:
			throw new Error(ERROR_ACTION_UNDEFINED);
		}

		return action;
	}

	// Low-Level Methods

	private boolean isInvalidCommand(String userCommand) {
		boolean isNullCommand = (userCommand == null);
		boolean isEmptyCommand = !isNullCommand && userCommand.trim().isEmpty();
		boolean isInvalidCommand = isNullCommand || isEmptyCommand;

		return isInvalidCommand;
	}

	private Message getHintPartialAdd(String userCommand) {
		String matchingCommandsString = getMatchingCommandsString(userCommand);

		boolean isPartialCommand = !matchingCommandsString.isEmpty();

		Message message;

		if (isPartialCommand) {
			message = getHintSuggestion(matchingCommandsString);
		} else {
			message = Add.getHint(userCommand);
		}
		return message;
	}

	private Message getHintDefault() {
		MessageType messageType = MessageType.HINT;

		Message defaultHint = new Message(messageType, HINT_MESSAGE_DEFAULT);

		return defaultHint;
	}

	private Message getHintSuggestion(String matchingCommandsString) {
		MessageType messageType = MessageType.HINT;

		String commandSuggestions = String.format(FORMAT_PARTIAL_SUGGESTIONS,
				matchingCommandsString);

		Message suggestionHint = new Message(messageType, commandSuggestions);

		return suggestionHint;
	}

	private String getMatchingCommandsString(String firstWord) {
		boolean isEmptyWord = firstWord.trim().isEmpty();

		if (isEmptyWord) {
			return "";
		}

		ArrayList<String[]> dictionaries = new ArrayList<String[]>();

		dictionaries.add(Delete.getDictionary());
		dictionaries.add(Done.getDictionary());
		dictionaries.add(Edit.getDictionary());
		dictionaries.add(Redo.getDictionary());
		dictionaries.add(Search.getDictionary());
		dictionaries.add(Undo.getDictionary());
		dictionaries.add(Undone.getDictionary());

		String lastComma = ", $";

		String matchingCommands = getPartialString(firstWord, dictionaries);
		matchingCommands = matchingCommands.replaceAll(lastComma, "");

		return matchingCommands;
	}

	private String getPartialString(String partialCommand,
			ArrayList<String[]> dictionaries) {
		String partialCommandLowerCase = partialCommand.toLowerCase();
		String matchingCommands = "";

		for (String[] dictionary : dictionaries) {
			for (String keyword : dictionary) {
				boolean isExactCommand = keyword
						.equals(partialCommandLowerCase);
				boolean isStartsWithKeyword = keyword
						.startsWith(partialCommandLowerCase);
				boolean isPartialCommand = !isExactCommand
						&& isStartsWithKeyword;

				if (isPartialCommand) {
					matchingCommands += "\"" + keyword + "\", ";
				}
			}
		}

		return matchingCommands;
	}
}