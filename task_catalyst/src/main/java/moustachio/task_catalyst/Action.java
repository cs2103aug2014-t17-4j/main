package moustachio.task_catalyst;

public abstract class Action {

	public abstract Message execute();

	public abstract Message undo();

}
