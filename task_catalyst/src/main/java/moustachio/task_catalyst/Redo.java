package moustachio.task_catalyst;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Redo extends Action {

	private static final String[] DICTIONARY = { "redo" };

	private static final String EXECUTE_ERROR = "There is nothing to redo.";

	List<Task> targetList;
	Task task;
	Stack<Action> undos;
	Stack<Action> redos;

	public Redo(List<Task> targetList, Stack<Action> undos, Stack<Action> redos) {
		this.targetList = targetList;
		this.undos = undos;
		this.redos = redos;
	}

	@Override
	public Message execute() {
		try {
			undos.push(redos.pop());
			Message message = undos.peek().execute();
			message.setMessage("(Redo) " + message.getMessage());
			return message;
		} catch (Exception e) {
			int type = Message.TYPE_ERROR;
			return new Message(type, EXECUTE_ERROR);
		}
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