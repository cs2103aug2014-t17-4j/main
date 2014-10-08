package moustachio.task_catalyst;

public class Hashtag extends Action {

	private static final String EXECUTE_SUCCESS = "Displaying hashtag category: %s.";
	String hashtag;

	public Hashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	@Override
	public Message execute() {
		int type = Message.TYPE_SUCCESS;
		String message = String.format(EXECUTE_SUCCESS, hashtag);
		return new Message(type, message);
	}

	@Override
	public Message undo() {
		return execute();
	}

	public static boolean isThisAction(String command) {
		return command.startsWith("#");
	}

	@Override
	public boolean isUndoable() {
		return false;
	}
}