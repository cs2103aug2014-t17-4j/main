package moustachio.task_catalyst;

//@author A0111890
public abstract class Action {
	public abstract Message execute();

	public abstract Message undo();

	public abstract boolean isUndoable();
}