package moustachio.task_catalyst;

import java.util.List;

public class Search extends Action {

	private static final String EXECUTE_SUCCESS = "Displaying search: %s.";
	private static final String EXECUTE_ERROR = "Invalid search term.";
	
	List<Task> targetList;
	String keyword;

	public Search(List<Task> targetList, String keyword) {
		this.targetList = targetList;
		this.keyword = keyword;
	}

	@Override
	public Message execute() {
		int type = Message.TYPE_SUCCESS;
		String message = String.format(EXECUTE_SUCCESS,keyword);
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