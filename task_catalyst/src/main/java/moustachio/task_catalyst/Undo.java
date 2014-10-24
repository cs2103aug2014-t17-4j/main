package moustachio.task_catalyst;

import java.util.Arrays;

public class Undo extends Action {

	private static final String[] DICTIONARY = { "undo" };

	private static final String EXECUTE_ERROR = "There is nothing to undo.";

	private static final String HINT_MESSAGE = "Undo: Press enter to undo task.";

	ActionInvoker actionInvoker;

	public Undo(String userCommand) {
		actionInvoker = ActionInvoker.getInstance();
	}

	@Override
	public Message execute() {
		Message message = actionInvoker.undoLastAction();
		if (message != null) {
			message.setMessage("(Undo) " + message.getMessage());
		} else {
			int type = Message.TYPE_ERROR;
			message = new Message(type, EXECUTE_ERROR);
		}
		return message;
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