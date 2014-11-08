package moustachio.task_catalyst;

import java.util.Stack;

//@author A0111890L
/**
 * ActionInvoker is used by ActionHintSystemActual for executing commands as
 * well as maintaining stacks for undo/redo functionality. It applies the
 * Singleton pattern as there should not be multiple action queues operation on
 * the same file.
 */
public class ActionInvoker {
	Stack<Action> undos;
	Stack<Action> redos;
	String defaultMessage;

	private static ActionInvoker instance;

	private ActionInvoker() {
		undos = new Stack<Action>();
		redos = new Stack<Action>();
	}

	public void testMode() {
		undos.clear();
		redos.clear();
	}

	public static ActionInvoker getInstance() {
		if (instance == null) {
			instance = new ActionInvoker();
		}

		return instance;
	}

	public void setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}

	public Message doAction(Action action) {
		Message message = new Message(MessageType.ERROR, defaultMessage);

		if (action != null) {
			message = action.execute();

			boolean isSuccess = (message.getType() == MessageType.SUCCESS);
			boolean isUndoable = action.isUndoable();

			if (isSuccess && isUndoable) {
				undos.push(action);
				redos.clear();
			}
		}

		return message;
	}

	public Message redoLastAction() {
		Message message;

		try {
			Action nextAction = redos.pop();
			undos.push(nextAction);

			message = nextAction.execute();
		} catch (Exception e) {
			message = null;
		}

		return message;
	}

	public Message undoLastAction() {
		Message message;

		try {
			Action previousAction = undos.pop();
			redos.push(previousAction);

			message = previousAction.undo();
		} catch (Exception e) {
			message = null;
		}

		return message;
	}
}
