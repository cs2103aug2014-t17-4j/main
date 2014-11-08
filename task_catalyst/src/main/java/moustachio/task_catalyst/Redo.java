package moustachio.task_catalyst;

import java.util.Arrays;
import java.util.List;

//@author A0111890L
/**
 * Redo Action relies on ActionInvoker for the actual redo operation, and
 * appends or generates the appropriate messages based on the outcome.
 */
public class Redo implements Action {
	private static final String[] DICTIONARY = { "redo" };

	private static final String EXECUTE_SUCCESS = "Redo: %s";
	private static final String EXECUTE_ERROR = "There is nothing to redo.";

	private static final String HINT_MESSAGE = "Redo: Hit enter to redo previous action.\nYou can also use CTRL+Y";

	ActionInvoker actionInvoker;

	public Redo(String userCommand) {
		actionInvoker = ActionInvoker.getInstance();
	}

	@Override
	public Message execute() {
		Message returnMessage = actionInvoker.redoLastAction();

		if (returnMessage != null) {
			String appendedMessage = String.format(EXECUTE_SUCCESS,
					returnMessage.getMessage());
			returnMessage.setMessage(appendedMessage);
		} else {
			MessageType messageType = MessageType.SUCCESS;
			returnMessage = new Message(messageType, EXECUTE_ERROR);
		}

		return returnMessage;
	}

	@Override
	public Message undo() {
		return execute();
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