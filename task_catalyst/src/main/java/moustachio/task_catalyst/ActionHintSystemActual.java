package moustachio.task_catalyst;

import java.util.ArrayList;

public class ActionHintSystemActual implements ActionHintSystem {

	ActionInvoker actionInvoker;

	public ActionHintSystemActual() {
		actionInvoker = ActionInvoker.getInstance();
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
			return new Message(Message.TYPE_HINT, "Type something to begin.");
		}

		CommandType COMMAND_TYPE = TaskCatalystCommons
				.getCommandType(userCommand);
		int type = Message.TYPE_HINT;
		String message;

		switch (COMMAND_TYPE) {
		case ADD:
			message = getMatchingCommands(userCommand);

			if (!message.isEmpty()) {
				return new Message(Message.TYPE_HINT, "Do you mean " + message
						+ "?");
			} else {
				try {
				message = TaskCatalystCommons
						.removeCurlyBraces(TaskCatalystCommons.removeSquareBrackets(TaskCatalystCommons
								.getPrettyString(TaskCatalystCommons
										.getInterpretedString(userCommand))));
				} catch (UnsupportedOperationException e) {
					message = "You cannot mix date types, and you can only specify one pair of date ranges per task.";
				}
				message += "\nAdd: You can include date information. Use []s to ignore processing.";
				// message = TaskBuilderAdvanced.interpretedString(userCommand);
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
			String taskNumberString = TaskCatalystCommons
					.getFirstWord(TaskCatalystCommons
							.removeFirstWord(userCommand));
			boolean endsWithSpace = userCommand.endsWith(" ");
			int taskNumber = TaskCatalystCommons.parseInt(taskNumberString);
			if (taskNumber > 0) {
				Task editTask;
				try {
					editTask = TaskManagerActual.getInstance().getDisplayList()
							.get(taskNumber - 1);
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
					message = TaskCatalystCommons
							.removeCurlyBraces(TaskCatalystCommons.removeSquareBrackets(TaskCatalystCommons.getPrettyString(TaskCatalystCommons.getInterpretedString(TaskCatalystCommons
									.removeFirstWord(TaskCatalystCommons
											.removeFirstWord(userCommand))))));
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
		dictionaries.add(Delete.getDictionary());
		dictionaries.add(Done.getDictionary());
		dictionaries.add(Edit.getDictionary());
		dictionaries.add(Redo.getDictionary());
		dictionaries.add(Search.getDictionary());
		dictionaries.add(Undo.getDictionary());
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
		default:
			action = null;
			break;
		}

		return action;
	}
}
