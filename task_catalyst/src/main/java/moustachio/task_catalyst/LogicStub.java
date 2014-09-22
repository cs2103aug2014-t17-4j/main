package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhenyu
 *
 */
public class LogicStub implements Logic {

	private static List<Task> ListOfTask = new ArrayList<Task>();
	private static List<String> ListOfHashtags = new ArrayList<String>();
	private static List<String> ListOfDefaultHashtags = new ArrayList<String>();

	public LogicStub() {
	}

	public Message processCommand(String userCommand) {
		return new Message(0, "stub from processCommand");
	}

	public List<Task> getList() {
		return ListOfTask;
	}

	public List<String> getDefaultHashtags() {
		return ListOfDefaultHashtags;
	}

	public List<String> getHashtags() {
		return ListOfHashtags;
	}

	public void addTask(String text) {
		ListOfTask.add(new Task(text));
	}

	public void addHashTag(String text) {
		ListOfHashtags.add(text);
	}

	public Message getMessageTyping(String userCommand) {
		return new Message(2, "testing get message while typing");
	}

}
