package moustachio.task_catalyst;

import java.util.Arrays;

public class Redo extends Action {

	private static final String[] DICTIONARY = { "redo" };

	private static final String EXECUTE_ERROR = "There is nothing to redo.";
	
	private static final String HINT_MESSAGE = "Redo: Press enter to redo task.";

	ActionInvoker actionInvoker;

	public Redo(String userCommand) {
		actionInvoker = ActionInvoker.getInstance();
	}

	@Override
	public Message execute() {
		Message message = actionInvoker.redoLastAction();
		if (message != null) {
			message.setMessage("(Redo) " + message.getMessage());
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