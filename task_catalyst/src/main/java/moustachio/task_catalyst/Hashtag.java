package moustachio.task_catalyst;

public class Hashtag extends Action {

	private static final String EXECUTE_SUCCESS = "Displaying hashtag category: #%s.";

	private static final String HINT_MESSAGE = "Hashtag: Hit enter after typing a valid hashtag to continue.";

	TaskManager taskManager;
	String hashtag;

	public Hashtag(String userCommand) {
		taskManager = TaskManagerActual.getInstance();
		hashtag = userCommand.replaceAll("#", "");
	}

	@Override
	public Message execute() {
		taskManager.setDisplayModeKeyword(DisplayMode.HASHTAG, hashtag);

		int type = Message.TYPE_SUCCESS;
		String message = String.format(EXECUTE_SUCCESS, hashtag);
		return new Message(type, message);
	}

	@Override
	public Message undo() {
		return execute();
	}

	public static Message getHint(String userCommand) {
		int type = Message.TYPE_HINT;
		Message returnMessage = new Message(type, HINT_MESSAGE);
		return returnMessage;
	}

	public static boolean isThisAction(String command) {
		return command.startsWith("#");
	}

	@Override
	public boolean isUndoable() {
		return false;
	}
}