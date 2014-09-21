package moustachio.task_catalyst;

import java.util.List;
import java.util.Stack;

public class Undo extends Action {

	private static final String EXECUTE_ERROR = "There is nothing to undo.";
	
	List<Task> targetList;
	Task task;
	Stack<Action> undos;
	Stack<Action> redos;

	public Undo(List<Task> targetList, Stack<Action> undos, Stack<Action> redos) {
		this.targetList = targetList;
		this.undos = undos;
		this.redos = redos;
	}

	@Override
	public Message execute() {
		try {
			redos.push(undos.pop());
			return redos.peek().undo();
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