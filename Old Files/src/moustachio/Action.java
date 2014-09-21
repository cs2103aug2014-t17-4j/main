package moustachio;

public abstract class Action {

	public abstract Message execute();

	public abstract Message undo();

}
