package moustachio.task_catalyst;

//@author A0111890L
/**
 * Hashtag Action parses the user's input and sets the TaskManager to use the
 * specified hashtag and the correct display mode.
 */
public class Hashtag implements Action {
	private static final String HINT_MESSAGE = "Hashtag: Hit enter after typing a valid hashtag to continue.";

	private static final String EXECUTE_SUCCESS = "Displaying hashtag category: #%s.";

	TaskManager taskManager;
	String hashtag;

	public Hashtag(String userCommand) {
		this.taskManager = TaskManagerActual.getInstance();
		this.hashtag = userCommand.replaceAll("#", "");
	}

	@Override
	public Message execute() {
		DisplayMode displayMode = DisplayMode.HASHTAG;
		String keyword = this.hashtag;

		this.taskManager.setDisplayModeKeyword(displayMode, keyword);

		MessageType messageType = MessageType.SUCCESS;
		String message = String.format(EXECUTE_SUCCESS, keyword);

		Message returnMessage = new Message(messageType, message);

		return returnMessage;
	}

	@Override
	public Message undo() {
		Message returnMessage = execute();

		return returnMessage;
	}

	public static Message getHint(String userCommand) {
		MessageType messageType = MessageType.HINT;
		String message = HINT_MESSAGE;

		Message returnMessage = new Message(messageType, message);

		return returnMessage;
	}

	public static boolean isThisAction(String command) {
		boolean isThisAction = command.startsWith("#");

		return isThisAction;
	}

	@Override
	public boolean isUndoable() {
		return false;
	}
}