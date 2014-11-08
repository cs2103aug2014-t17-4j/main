package moustachio.task_catalyst;

//@author A0111890L
/**
 * Action is used to encapsulate a command's implementation and all its related
 * hints.
 */
public interface Action {
	public abstract Message execute();

	public abstract Message undo();

	public abstract boolean isUndoable();
}