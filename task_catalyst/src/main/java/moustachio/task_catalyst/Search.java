package moustachio.task_catalyst;

import java.util.Arrays;

public class Search extends Action {

	private static final String[] DICTIONARY = { "search" };

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

	public static boolean isThisAction(String command) {
		return Arrays.asList(DICTIONARY).contains(command);
	}
	
	public static String[] getDictionary() {
		return DICTIONARY;
	}

	@Override
	public boolean isUndoable() {
		return false;
	}
}