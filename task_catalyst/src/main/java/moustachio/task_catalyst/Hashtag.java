package moustachio.task_catalyst;

//@author A0111890L
/**
 * Hashtag Action parses the user's input and sets the TaskManager to use the
 * specified hashtag and the correct display mode.
 */
public class Hashtag implements Action {
	private static final String HINT_MESSAGE = "Hashtag: Hit enter after typing a valid hashtag to continue.";

	private static final String EXECUTE_SUCCESS = "Displaying hashtag category: #%s.";
	private static final String EXECUTE_ERROR = "Please enter a valid hashtag.";

	TaskManager taskManager;
	String hashtag;

	public Hashtag(String userCommand) {
		this.taskManager = TaskManagerActual.getInstance();
		this.hashtag = userCommand.replaceAll("#", "");
	}

	@Override
	public Message execute() {
		MessageType messageType;
		String message;

		DisplayMode displayMode = DisplayMode.HASHTAG;
		String keyword = this.hashtag;

		boolean isHashtagEmpty = this.hashtag.isEmpty();

		if (isHashtagEmpty) {
			messageType = MessageType.ERROR;
			message = EXECUTE_ERROR;
		} else {
			this.taskManager.setDisplayModeKeyword(displayMode, keyword);

			messageType = MessageType.SUCCESS;
			message = String.format(EXECUTE_SUCCESS, keyword);
		}

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