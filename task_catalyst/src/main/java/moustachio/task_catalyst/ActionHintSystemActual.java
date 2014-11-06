package moustachio.task_catalyst;

import java.util.ArrayList;

public class ActionHintSystemActual implements ActionHintSystem {

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

	@Override
	public Message processCommand(String userCommand) {
		Action action = generateAction(userCommand);
		Message message = actionInvoker.doAction(action);
		return message;
	}

	@Override
	public Message getMessageTyping(String userCommand) {

		if (userCommand == null || userCommand.trim().isEmpty()) {
			return new Message(Message.TYPE_HINT, HINT_MESSAGE_DEFAULT);
		}

		CommandType COMMAND_TYPE = TaskCatalystCommons
				.getCommandType(userCommand);

		Message message;

		switch (COMMAND_TYPE) {
		case ADD:
			message = getHintPartialOrAdd(userCommand);
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
		default:
			action = null;
			break;
		}

		return action;
	}

	// Low-Level Methods

	private Message getHintPartialOrAdd(String userCommand) {
		Message message;
		String matchingCommandsString = getMatchingCommandsString(userCommand);

		boolean isPartialCommand = !matchingCommandsString.isEmpty();

		if (isPartialCommand) {
			message = getHintSuggestion(matchingCommandsString);
		} else {
			message = Add.getHint(userCommand);
		}
		return message;
	}

	private Message getHintDefault() {
		int type = Message.TYPE_HINT;
		Message defaultHint = new Message(type, HINT_MESSAGE_DEFAULT);
		return defaultHint;
	}

	private Message getHintSuggestion(String matchingCommandsString) {
		int type = Message.TYPE_HINT;
		String commandSuggestions = String.format(FORMAT_PARTIAL_SUGGESTIONS,
				matchingCommandsString);
		Message suggestionHint = new Message(type, commandSuggestions);
		return suggestionHint;
	}

	private String getMatchingCommandsString(String firstWord) {

		if (firstWord.trim().isEmpty()) {
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

		String matchingCommands = getPartialString(firstWord, dictionaries);
		matchingCommands = matchingCommands.replaceAll(", $", "");

		return matchingCommands;
	}

	private String getPartialString(String partialCommand,
			ArrayList<String[]> dictionaries) {
		String matchingCommands = "";
		for (String[] dictionary : dictionaries) {
			for (String keyword : dictionary) {
				if (!keyword.equalsIgnoreCase(partialCommand)
						&& keyword.startsWith(partialCommand.toLowerCase())) {
					matchingCommands += "\"" + keyword + "\", ";
				}
			}
		}
		return matchingCommands;
	}
}
