package moustachio.task_catalyst;

import java.util.List;

public class Hashtag extends Action {

	private static final String EXECUTE_SUCCESS = "Displaying hashtag category: %s.";
	private static final String EXECUTE_ERROR = "Invalid hashtag.";
	List<Task> targetList;
	String hashtag;

	public Hashtag(List<Task> targetList, String hashtag) {
		this.targetList = targetList;
		this.hashtag = hashtag;
	}

	@Override
	public Message execute() {
		int type = Message.TYPE_SUCCESS;
		String message = String.format(EXECUTE_SUCCESS,hashtag);
		return new Message(type, message);
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