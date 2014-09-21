package moustachio.task_catalyst;

public class Search extends Action {

	private static final String EXECUTE_SUCCESS = "Displaying search: %s.";

	String keyword;

	public Search(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public Message execute() {
		int type = Message.TYPE_SUCCESS;
		String message = String.format(EXECUTE_SUCCESS, keyword);
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