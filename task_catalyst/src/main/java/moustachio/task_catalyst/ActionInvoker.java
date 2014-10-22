package moustachio.task_catalyst;

import java.util.Stack;

public class ActionInvoker {

	Stack<Action> undos;
	Stack<Action> redos;

	private static ActionInvoker instance;

	public static ActionInvoker getInstance() {
		if (instance == null) {
			instance = new ActionInvoker();
		}
		return instance;
	}
	
	public void testMode() {
		undos.clear();
		redos.clear();
	}

	private ActionInvoker() {
		undos = new Stack<Action>();
		redos = new Stack<Action>();
	}

	public Message doAction(Action action) {
		Message message = new Message(Message.TYPE_ERROR,
				"Type something to begin.");
		if (action != null) {
			message = action.execute();
			boolean isSuccess = message.getType() == Message.TYPE_SUCCESS;
			boolean isUndoable = action.isUndoable();
			if (isSuccess && isUndoable) {
				undos.push(action);
				redos.clear();
			}
		}
		return message;
	}

	public Message redoLastAction() {
		try {
			undos.push(redos.pop());
			Message message = undos.peek().execute();
			return message;
		} catch (Exception e) {
			return null;
		}
	}

	public Message undoLastAction() {
		try {
			redos.push(undos.pop());
			Message message = redos.peek().undo();
			return message;
		} catch (Exception e) {
			return null;
		}
	}
}
