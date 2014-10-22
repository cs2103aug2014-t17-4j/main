package moustachio.task_catalyst;

import java.util.Arrays;

public class Undo extends Action {

	private static final String[] DICTIONARY = { "undo" };

	private static final String EXECUTE_ERROR = "There is nothing to undo.";

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