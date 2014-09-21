package moustachio.task_catalyst;

import java.util.List;
import java.util.Stack;

public class Redo extends Action {

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
			return undos.peek().execute();
		} catch (Exception e) {
			int type = Message.TYPE_ERROR;
			return new Message(type, EXECUTE_ERROR);
		}
	}

	@Override
	public Message undo() {
		return execute();
	}

	@Override
	public boolean isUndoable() {
		return false;
	}
}