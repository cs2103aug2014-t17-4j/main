package moustachio;

public abstract class UndoableAction {

	public abstract Message execute();

	public abstract Message undo();

}
